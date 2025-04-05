package pe.edu.vallegrande.FoodCost.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.FoodCost.dto.reception.FoodDto;
import pe.edu.vallegrande.FoodCost.dto.reception.HensDto;
import pe.edu.vallegrande.FoodCost.dto.transfer.FoodCostRequestDto;
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import pe.edu.vallegrande.FoodCost.repository.FoodCostsRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InsertCostService {

    private final WebClient webClient;
    private final FoodCostsRepository foodCostsRepository;

    @Value("${external.food-service-url}")
    private String foodServiceUrl;

    @Value("${external.hens-service-url}")
    private String hensServiceUrl;

    public Mono<Void> addFoodCost(FoodCostRequestDto request) {

        Mono<FoodDto> foodMono = webClient.get()
                .uri(foodServiceUrl)
                .retrieve()
                .bodyToFlux(FoodDto.class)
                .filter(f -> f.getFoodType().equalsIgnoreCase(request.getFoodType()))
                .next() // Toma solo el primer alimento que coincida
                .switchIfEmpty(Mono
                        .error(new RuntimeException("No se encontró alimento para el tipo: " + request.getFoodType())));

        Mono<HensDto> hensMono = webClient.get()
                .uri(hensServiceUrl)
                .retrieve()
                .bodyToFlux(HensDto.class)
                .next();

        return Mono.zip(foodMono, hensMono)
                .flatMap(tuple -> {
                    FoodDto food = tuple.getT1();
                    HensDto hens = tuple.getT2();

                    if (food.getAmount() == null
                            || BigDecimal.valueOf(food.getAmount()).compareTo(BigDecimal.ZERO) == 0) {
                        return Mono.error(new RuntimeException(
                                "Cantidad inválida de alimento para el tipo: " + request.getFoodType()));
                    }

                    // Cálculo de kg, costo y fechas
                    BigDecimal totalKg = BigDecimal.valueOf(hens.getQuantity())
                            .multiply(request.getGramsPerChicken())
                            .multiply(BigDecimal.valueOf(7))
                            .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);

                    BigDecimal costPerKg = request.getUnitPrice()
                            .divide(BigDecimal.valueOf(food.getAmount()), 2, RoundingMode.HALF_UP);

                    BigDecimal totalCost = costPerKg.multiply(totalKg).setScale(2, RoundingMode.HALF_UP);

                    return foodCostsRepository.findTopByOrderByStartDateDesc()
                            .flatMap(lastFoodCost -> {
                                // Si es el primer registro, usamos arrivalDate de las gallinas
                                LocalDate startDate = lastFoodCost != null ? lastFoodCost.getEndDate().plusDays(1)
                                        : hens.getArrivalDate();
                                LocalDate endDate = startDate.plusDays(6);

                                // Crear objeto FoodCost
                                FoodCost foodCost = new FoodCost();
                                foodCost.setWeekNumber(request.getWeekNumber());
                                foodCost.setFoodType(request.getFoodType());
                                foodCost.setGramsPerChicken(request.getGramsPerChicken());
                                foodCost.setTotalKg(totalKg);
                                foodCost.setTotalCost(totalCost);
                                foodCost.setStartDate(startDate);
                                foodCost.setEndDate(endDate);

                                return foodCostsRepository.save(foodCost).then();
                            });
                });
    }

}
