package pe.edu.vallegrande.foods.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.foods.dto.FoodRequest;
import pe.edu.vallegrande.foods.exception.FoodInactiveException;
import pe.edu.vallegrande.foods.exception.FoodNotFoundException;
import pe.edu.vallegrande.foods.model.Food;
import pe.edu.vallegrande.foods.repository.FoodRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FoodService {

    private static final String FOOD_NOT_FOUND_MESSAGE = "Food not found";
    private static final String INACTIVE_FOOD_UPDATE_ERROR = "Cannot update inactive food";
    private static final String FOOD_ALREADY_INACTIVE = "Food is already inactive";
    private static final String FOOD_ALREADY_ACTIVE = "Food is already active";

    private final FoodRepository foodRepository;

    // Método para obtener todos los alimentos
    public Flux<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    // Método para obtener todos los alimentos con estado 'A'
    public Flux<Food> getAllActiveFoods() {
        return foodRepository.findAllByStatus("A");
    }

    // Método para obtener todos los alimentos inactivos (estado 'I')
    public Flux<Food> getAllInactiveFoods() {
        return foodRepository.findAllByStatus("I");
    }

    // Método para obtener alimentos por tipo (food_type)
    public Flux<Food> getFoodsByType(String foodType) {
        return foodRepository.findByFoodTypeContaining(foodType);
    }

    // Método para guardar un nuevo alimento
    public Mono<Food> createFood(FoodRequest request) {
        Food food = new Food();
        food.setFoodType(request.getFoodType());
        food.setFoodBrand(request.getFoodBrand());
        food.setAmount(request.getAmount());
        food.setPackaging(request.getPackaging());
        food.setUnitMeasure(request.getUnitMeasure());
        return foodRepository.save(food);
    }

    // Método para actualizar un alimento
    public Mono<Food> updateFood(Long id, FoodRequest foodRequest) {
        return foodRepository.findById(id)
                .switchIfEmpty(Mono.error(new FoodNotFoundException(FOOD_NOT_FOUND_MESSAGE)))
                .flatMap(existingFood -> {
                    if ("A".equals(existingFood.getStatus())) {
                        existingFood.setFoodType(foodRequest.getFoodType());
                        existingFood.setFoodBrand(foodRequest.getFoodBrand());
                        existingFood.setAmount(foodRequest.getAmount());
                        existingFood.setPackaging(foodRequest.getPackaging());
                        existingFood.setUnitMeasure(foodRequest.getUnitMeasure());
                        return foodRepository.save(existingFood);
                    }
                    return Mono.error(new FoodInactiveException(INACTIVE_FOOD_UPDATE_ERROR));
                });
    }

    // Método para eliminar un alimento lógicamente
    public Mono<Food> deleteFoodLogically(Long id) {
        return foodRepository.findById(id)
                .switchIfEmpty(Mono.error(new FoodNotFoundException(FOOD_NOT_FOUND_MESSAGE)))
                .flatMap(existingFood -> {
                    if ("A".equals(existingFood.getStatus())) {
                        existingFood.setStatus("I");
                        return foodRepository.save(existingFood);
                    }
                    return Mono.error(new FoodInactiveException(FOOD_ALREADY_INACTIVE));
                });
    }

    // Método para restaurar un alimento (cambiar estado de 'I' a 'A')
    public Mono<Food> restoreFood(Long id) {
        return foodRepository.findById(id)
                .switchIfEmpty(Mono.error(new FoodNotFoundException(FOOD_NOT_FOUND_MESSAGE)))
                .flatMap(existingFood -> {
                    if ("I".equals(existingFood.getStatus())) {
                        existingFood.setStatus("A");
                        return foodRepository.save(existingFood);
                    }
                    return Mono.error(new FoodInactiveException(FOOD_ALREADY_ACTIVE));
                });
    }

    // Método para eliminar un alimento físicamente de la base de datos
    public Mono<Void> deleteFoodPhysically(Long id) {
        return foodRepository.findById(id)
                .switchIfEmpty(Mono.error(new FoodNotFoundException(FOOD_NOT_FOUND_MESSAGE)))
                .flatMap(existingFood -> foodRepository.deleteById(id));
    }

}
