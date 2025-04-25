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

    // Método para obtener costo de alimentos activos
    public Flux<FoodCost> getAllActiveCosts() {
        return foodCostsRepository.findAllByStatusOrderByIdFoodCostsAsc("A");
    }

    // Método para obtener costo de alimentos inactivos
    public Flux<FoodCost> getAllInactiveCosts() {
        return foodCostsRepository.findAllByStatusOrderByIdFoodCostsAsc("I");
    }

    // Método para obtener costo de alimentos por semana (week_number)
    public Flux<FoodCost> getByWeekNumber(String weekNumber){
        return foodCostsRepository.findByWeekNumber(weekNumber);
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
