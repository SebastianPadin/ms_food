package pe.edu.vallegrande.foodcost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.foodcost.exception.service.FoodCostInactiveException;
import pe.edu.vallegrande.foodcost.exception.service.FoodCostNotFoundException;
import pe.edu.vallegrande.foodcost.model.FoodCost;
import pe.edu.vallegrande.foodcost.repository.FoodCostsRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FoodCostsService {

    public static final String RECORD_NOT_FOUND_MESSAGE = "Record not found";


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
                .switchIfEmpty(Mono.error(new FoodCostNotFoundException(RECORD_NOT_FOUND_MESSAGE)))
                .flatMap(existingFoodCosts -> {
                    if ("A".equals(existingFoodCosts.getStatus())) {
                        existingFoodCosts.setStatus("I");
                        return foodCostsRepository.save(existingFoodCosts);
                    }
                    return Mono.error(new FoodCostInactiveException("The registration is already inactive"));
                });
    }

    // Método para restaurar el costo de alimento (cambiar estado de 'I' a 'A')
    public Mono<FoodCost> restoreFoodCosts(Long id) {
        return foodCostsRepository.findById(id)
                .switchIfEmpty(Mono.error(new FoodCostNotFoundException(RECORD_NOT_FOUND_MESSAGE)))
                .flatMap(existingFoodCosts -> {
                    if ("I".equals(existingFoodCosts.getStatus())) {
                        existingFoodCosts.setStatus("A");
                        return foodCostsRepository.save(existingFoodCosts);
                    }
                    return Mono.error(new FoodCostInactiveException("Record is already active"));
                });
    }

    // Método para eliminar un costo de alimento físicamente
    public Mono<Void> deleteFoodCostPhysically(Long id) {
        return foodCostsRepository.findById(id)
                .switchIfEmpty(Mono.error(new FoodCostNotFoundException(RECORD_NOT_FOUND_MESSAGE)))
                .flatMap(existingFoodCost -> foodCostsRepository.deleteById(id));
    }
}
