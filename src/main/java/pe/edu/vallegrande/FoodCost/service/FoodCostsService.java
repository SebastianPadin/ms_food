package pe.edu.vallegrande.FoodCost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import pe.edu.vallegrande.FoodCost.repository.FoodCostsRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FoodCostsService {

    private final FoodCostsRepository foodCostsRepository;

    
    public Flux<FoodCost> getAllActiveCosts() {
        return foodCostsRepository.findAllByStatus("A");
    }

    public Flux<FoodCost> getAllInactiveCosts() {
        return foodCostsRepository.findAllByStatus("I");
    }

    public Mono<FoodCost> createFoodCosts(FoodCost foodcosts) {
        return foodCostsRepository.save(foodcosts);
    }

    public Mono<FoodCost> updateFoodCosts(Long id, FoodCost food) {
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
