package pe.edu.vallegrande.foods.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.foods.model.FoodCosts;
import pe.edu.vallegrande.foods.repository.FoodCostsRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FoodCostsService {

    private final FoodCostsRepository foodCostsRepository;

    
    public Flux<FoodCosts> getAllActiveCosts() {
        return foodCostsRepository.findAllByStatus("A");
    }

    public Flux<FoodCosts> getAllInactiveCosts() {
        return foodCostsRepository.findAllByStatus("I");
    }

    public Mono<FoodCosts> createFoodCosts(FoodCosts foodcosts) {
        return foodCostsRepository.save(foodcosts);
    }

    public Mono<FoodCosts> updateFoodCosts(Long id, FoodCosts food) {
        return foodCostsRepository.findById(id)
                .flatMap(existingFoodCosts -> {
                    if ("A".equals(existingFoodCosts.getStatus())) {

                        existingFoodCosts.setWeekNumber(food.getWeekNumber());
                        existingFoodCosts.setFoodId(food.getFoodId());
                        existingFoodCosts.setGramsPerChicken(food.getGramsPerChicken());
                        existingFoodCosts.setTotalKg(food.getTotalKg());
                        existingFoodCosts.setTotalCost(food.getTotalCost());
                        existingFoodCosts.setStartDate(food.getStartDate());
                        existingFoodCosts.setEndDate(food.getEndDate());

                        return foodCostsRepository.save(existingFoodCosts);
                    } else {
                        return Mono.error(new RuntimeException(
                                "No se pueden editar los costos de alimentos con el estado inactivo"));
                    }
                })
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontró el registro de costos de alimentos")));
    }

    // Método para eliminar un costo de alimento lógicamente
    public Mono<FoodCosts> deleteFoodCost(Long id) {
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
    public Mono<FoodCosts> restoreFoodCosts(Long id) {
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
