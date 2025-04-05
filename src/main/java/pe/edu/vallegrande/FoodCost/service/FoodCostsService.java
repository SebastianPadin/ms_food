package pe.edu.vallegrande.FoodCost.service;

import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.FoodCost.dto.FoodDto;
import pe.edu.vallegrande.FoodCost.dto.HensDto;
import pe.edu.vallegrande.FoodCost.dto.InsertCostRequestDto;
import pe.edu.vallegrande.FoodCost.dto.UpdateCostRequestDto;
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import pe.edu.vallegrande.FoodCost.repository.FoodCostsRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class FoodCostsService {

    private final WebClient webClient;
    private final FoodCostsRepository foodCostsRepository;

    @Value("${external.food-service-url}")
    private String foodServiceUrl;

    @Value("${external.hens-service-url}")
    private String hensServiceUrl;

    public Flux<FoodCost> getAllActiveCosts() {
        return foodCostsRepository.findAllByStatusOrderByIdFoodCostsAsc("A");
    }

    public Flux<FoodCost> getAllInactiveCosts() {
        return foodCostsRepository.findAllByStatusOrderByIdFoodCostsAsc("I");
    }

    public Mono<Void> addFoodCost(InsertCostRequestDto request) {

        Mono<FoodDto> foodMono = webClient.get()
                .uri(foodServiceUrl)
                .retrieve()
                .bodyToFlux(FoodDto.class)
                .filter(f -> f.getFoodType().equalsIgnoreCase(request.getFoodType()))
                .next();

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
                        return Mono.error(
                                new RuntimeException("Cantidad inválida de alimento para " + request.getFoodType()));
                    }

                    // Total kg = ((cantidad_gallinas * gramos_por_gallina * 7 días) / 1000)
                    BigDecimal totalKg = BigDecimal.valueOf(hens.getQuantity())
                            .multiply(request.getGramsPerChicken())
                            .multiply(BigDecimal.valueOf(7))
                            .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);

                    // Costo por kg = precio_unitario / cantidad_kg_saco
                    BigDecimal costPerKg = request.getUnitPrice()
                            .divide(BigDecimal.valueOf(food.getAmount()), 2, RoundingMode.HALF_UP);

                    // Costo total = costo_kg * total_kg
                    BigDecimal totalCost = costPerKg.multiply(totalKg).setScale(2, RoundingMode.HALF_UP);

                    // Rango de fechas
                    LocalDate startDate = hens.getArrivalDate(); // Semana 1 inicia con arrivalDate
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
                    foodCost.setStatus("A");

                    return foodCostsRepository.save(foodCost).then();
                });
    }

    public Mono<Void> updateFoodCost(Integer idFoodCosts, UpdateCostRequestDto dto) {
        return foodCostsRepository.updateFoodCost(
                idFoodCosts,
                dto.getWeekNumber(),
                dto.getFoodId(),
                dto.getGramsPerChicken(),
                dto.getChickensCount(),
                dto.getUnitPrice());
    }

    // Método para eliminar un costo de alimento lógicamente
    public Mono<FoodCost> deleteFoodCost(Long id) {
        return foodCostsRepository.findById(id)
                .flatMap(existingFoodCosts -> {
                    if ("A".equals(existingFoodCosts.getStatus())) {
                        existingFoodCosts.setStatus("I");
                        return foodCostsRepository.save(existingFoodCosts);
                    } else {
                        return Mono.error(new RuntimeException("The registration is already inactive"));
                    }
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Record not found")));
    }

    // Método para restaurar el costo de alimento (cambiar estado de 'I' a 'A')
    public Mono<FoodCost> restoreFoodCosts(Long id) {
        return foodCostsRepository.findById(id)
                .flatMap(existingFoodCosts -> {
                    if ("I".equals(existingFoodCosts.getStatus())) {
                        existingFoodCosts.setStatus("A");
                        return foodCostsRepository.save(existingFoodCosts);
                    } else {
                        return Mono.error(new RuntimeException("Record is already active"));
                    }
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Record not found")));
    }

}
