package pe.edu.vallegrande.foods.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import pe.edu.vallegrande.foods.dto.FoodRequest;
import pe.edu.vallegrande.foods.model.Food;
import pe.edu.vallegrande.foods.repository.FoodRepository;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodService foodService;

    @Test
    void testGetAllActiveFoods() {
        // 👉 Paso 1: Crear alimento simulado con estado "A"
        Food food = new Food();
        food.setIdFood(1L);
        food.setFoodType("Crecimiento");
        food.setFoodBrand("Avifort");
        food.setAmount(50);
        food.setPackaging("Saco");
        food.setUnitMeasure("kg");
        food.setEntryDate(LocalDate.parse("2023-01-12"));
        food.setStatus("A");

        List<Food> activeFoods = List.of(food);

        // 👉 Paso 2: Simular que el repositorio retorna esos alimentos activos
        when(foodRepository.findAllByStatus("A")).thenReturn(Flux.fromIterable(activeFoods));

        // 👉 Paso 3: Ejecutar el servicio y verificar con StepVerifier
        StepVerifier.create(foodService.getAllActiveFoods())
                .expectNextMatches(result -> "A".equals(result.getStatus()))
                .verifyComplete();

        // 👉 Paso 4: Verificar que el repositorio fue llamado con "A"
        verify(foodRepository).findAllByStatus("A");
    }

    @Test
    void testCreateFood() {
        // Fake input request
        FoodRequest request = new FoodRequest("Postura", "Dekalb Brown", 25, "Saco", "kg");
        Food fakeSavedFood = new Food();
        fakeSavedFood.setIdFood(1L);
        fakeSavedFood.setStatus("A");
        fakeSavedFood.setFoodType("Postura");
        fakeSavedFood.setFoodBrand("Dekalb Brown");
        fakeSavedFood.setAmount(25);
        fakeSavedFood.setPackaging("Saco");
        fakeSavedFood.setUnitMeasure("kg");

        // Mock: cuando se llame a save, devolver el fakeSavedFood
        when(foodRepository.save(any(Food.class))).thenReturn(Mono.just(fakeSavedFood));

        // Ejecutar el servicio
        StepVerifier.create(foodService.createFood(request))
                .expectNextMatches(food -> "Postura".equals(food.getFoodType())
                        && "Dekalb Brown".equals(food.getFoodBrand())
                        && "A".equals(food.getStatus()))
                .verifyComplete();

        // Verificar que se llamó a save()
        verify(foodRepository).save(any(Food.class));
    }

    @Test
    void testDeleteFoodLogically() {
        // 👉 Paso 1: Arrange - Crear un alimento simulado (activo)
        Food existingFood = new Food();
        existingFood.setIdFood(1L);
        existingFood.setStatus("A");
        existingFood.setFoodType("Bread");
        existingFood.setFoodBrand("BrandZ");
        existingFood.setAmount(3);
        existingFood.setPackaging("Pack");
        existingFood.setUnitMeasure("g");

        // 👉 Paso 2: Simular que se encuentra ese alimento en la BD
        when(foodRepository.findById(1L)).thenReturn(Mono.just(existingFood));

        // 👉 Paso 3: Simular la respuesta del save con el estado actualizado a "I"
        Food deletedFood = new Food();
        deletedFood.setIdFood(1L);
        deletedFood.setStatus("I"); // Simulamos que el servicio cambia el estado
        deletedFood.setFoodType("Bread");
        deletedFood.setFoodBrand("BrandZ");
        deletedFood.setAmount(3);
        deletedFood.setPackaging("Pack");
        deletedFood.setUnitMeasure("g");

        when(foodRepository.save(any(Food.class))).thenReturn(Mono.just(deletedFood));

        // 👉 Paso 4: Act & Assert con StepVerifier
        StepVerifier.create(foodService.deleteFoodLogically(1L))
                .expectNextMatches(food -> "I".equals(food.getStatus()))
                .verifyComplete();

        // 👉 Paso 5: Verificar que los métodos del mock fueron llamados
        verify(foodRepository).findById(1L);
        verify(foodRepository).save(any(Food.class));
    }
}
