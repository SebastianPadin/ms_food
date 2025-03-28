package pe.edu.vallegrande.FoodCost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.FoodCost.dto.InsertCostRequestDto;
import pe.edu.vallegrande.FoodCost.dto.UpdateCostRequestDto;
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import pe.edu.vallegrande.FoodCost.repository.FoodCostsRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FoodCostsService {

    private final FoodCostsRepository foodCostsRepository;

    public Flux<FoodCost> getAllActiveCosts() {
        return foodCostsRepository.findAllByStatusOrderByIdFoodCostsAsc("A");
    }

    public Flux<FoodCost> getAllInactiveCosts() {
        return foodCostsRepository.findAllByStatusOrderByIdFoodCostsAsc("I");
    }

    public Mono<Void> addFoodCost(InsertCostRequestDto dto) {
        return foodCostsRepository.insertFoodCost(
                dto.getWeekNumber(),
                dto.getFoodId(),
                dto.getGramsPerChicken(),
                dto.getChickensCount(),
                dto.getUnitPrice());
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
