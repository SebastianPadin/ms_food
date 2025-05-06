/**
 * Paquete que gestiona la lógica de negocio de los alimentos.
 */
package pe.edu.vallegrande.foods.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.foods.dto.FoodRequest;
import pe.edu.vallegrande.foods.model.Food;
import pe.edu.vallegrande.foods.repository.FoodRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FoodService {

    /**
     * Repositorio para acceder a los datos de alimentos.
     */
    private final FoodRepository foodRepository;

    /**
     * Lista todos los alimentos.
     * 
     * @return Todos los alimentos en la bd.
     *
     */
    public Flux<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    /**
     * Lista todos los alimentos activos.
     * 
     * @return Alimento con estado A.
     *
     */
    public Flux<Food> getAllActiveFoods() {
        return foodRepository.findAllByStatus("A");
    }

    /**
     * Lista todos los alimentos inactivos.
     * 
     * @return Alimento con estado I.
     *
     */
    public Flux<Food> getAllInactiveFoods() {
        return foodRepository.findAllByStatus("I");
    }

    /**
     * Filtra alimentos por tipo.
     * 
     * @param foodType Tipo de alimento que desea filtrar.
     * @return Alimento filtrado.
     *
     */
    public Flux<Food> getFoodsByType(final String foodType) {
        return foodRepository.findByFoodTypeContaining(foodType);
    }

    /**
     * Inserta un nuevo alimento
     *
     * @param foodRequest Datos enviados del alimento.
     * @return Alimento insertado.
     */
    public Mono<Food> createFood(final FoodRequest request) {
        Food food = new Food();
        food.setFoodType(request.getFoodType());
        food.setFoodBrand(request.getFoodBrand());
        food.setAmount(request.getAmount());
        food.setPackaging(request.getPackaging());
        food.setUnitMeasure(request.getUnitMeasure());
        return foodRepository.save(food);
    }

    /**
     * Actualiza los datos de un alimento.
     *
     * @param id          Identificador del alimento.
     * @param foodRequest Datos actualizados del alimento.
     * @return Alimento actualizado.
     */
    public Mono<Food> updateFood(final Long id, final FoodRequest foodRequest) {
        return foodRepository.findById(id)
                .flatMap(existingFood -> {
                    if ("A".equals(existingFood.getStatus())) {
                        existingFood.setFoodType(foodRequest.getFoodType());
                        existingFood.setFoodBrand(foodRequest.getFoodBrand());
                        existingFood.setAmount(foodRequest.getAmount());
                        existingFood.setPackaging(foodRequest.getPackaging());
                        existingFood.setUnitMeasure(foodRequest.getUnitMeasure());
                        return foodRepository.save(existingFood);
                    }
                    return Mono.error(new RuntimeException("No se puede editar un alimento con estado inactivo"));
                });
    }

    /**
     * Elimina un registro de manera lógica.
     *
     * @param id Identificador del alimento.
     * @return Alimento eliminado.
     */
    public Mono<Food> deleteFoodLogically(final Long id) {
        return foodRepository.findById(id)
            .flatMap(existingFood -> {
                if ("A".equals(existingFood.getStatus())) {
                    existingFood.setStatus("I");
                    return foodRepository.save(existingFood);
                } else {
                    return Mono.error(new RuntimeException("Food is already inactive"));
                }
            })
            .switchIfEmpty(Mono.error(new RuntimeException("Food not found")));
    }

    /**
     * Restaura un registro de alimento.
     *
     * @param id Identificador del alimento.
     * @return Alimento eliminado restaurado.
     */
    public Mono<Food> restoreFood(final Long id) {
        return foodRepository.findById(id)
            .flatMap(existingFood -> {
                if ("I".equals(existingFood.getStatus())) {
                    existingFood.setStatus("A");
                    return foodRepository.save(existingFood);
                } else {
                    return Mono.error(new RuntimeException("Food is already active"));
                }
            })
            .switchIfEmpty(Mono.error(new RuntimeException("Food not found")));
    }
}
