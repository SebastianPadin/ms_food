package pe.edu.vallegrande.FoodCost.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.FoodCost.dto.reception.FoodDto;
import pe.edu.vallegrande.FoodCost.dto.reception.HensDto;
import pe.edu.vallegrande.FoodCost.dto.transfer.FoodCostRequestDto;
import pe.edu.vallegrande.FoodCost.repository.FoodCostsRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpdateCostService {

    private final WebClient webClient;
    private final FoodCostsRepository foodCostsRepository;

    @Value("${external.food-service-url}")
    private String foodServiceUrl;

    @Value("${external.hens-service-url}")
    private String hensServiceUrl;

    public Mono<Void> updateFoodCost(Long idFoodCosts, FoodCostRequestDto request) {

        return foodCostsRepository.findById(idFoodCosts)
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontró el registro con ID: " + idFoodCosts)))
                .flatMap(existing -> {

                    Mono<FoodDto> foodMono = webClient.get()
                            .uri(foodServiceUrl)
                            .retrieve()
                            .bodyToFlux(FoodDto.class)
                            .filter(f -> f.getFoodType().equalsIgnoreCase(request.getFoodType()))
                            .next()
                            .switchIfEmpty(Mono.error(new RuntimeException(
                                    "No se encontró alimento del tipo: " + request.getFoodType())));

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
                                    return Mono.error(new RuntimeException("Cantidad inválida de alimento"));
                                }

                                BigDecimal totalKg = BigDecimal.valueOf(hens.getQuantity())
                                        .multiply(request.getGramsPerChicken())
                                        .multiply(BigDecimal.valueOf(7))
                                        .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);

                                BigDecimal costPerKg = request.getUnitPrice()
                                        .divide(BigDecimal.valueOf(food.getAmount()), 2, RoundingMode.HALF_UP);

                                BigDecimal totalCost = costPerKg.multiply(totalKg).setScale(2, RoundingMode.HALF_UP);

                                // Actualizamos solo los campos editables
                                existing.setWeekNumber(request.getWeekNumber());
                                existing.setFoodType(request.getFoodType());
                                existing.setGramsPerChicken(request.getGramsPerChicken());
                                existing.setTotalKg(totalKg);
                                existing.setTotalCost(totalCost);

                                return foodCostsRepository.save(existing).then();
                            });
                });
    }
}
