package pe.edu.vallegrande.foods.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.vallegrande.foods.model.Food;
import pe.edu.vallegrande.foods.repository.FoodRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.LocalDate;
import java.util.List;

class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodService foodService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. Prueba para obtener todos los alimentos activos
    @Test
    void testGetAllActiveFoods() {
        Food food1 = new Food(1L, "Inicio de gallina ponedora", "Dekalb Brown", "50", "Saco", "kg",
                LocalDate.of(2025, 1, 31), "A");
        Food food2 = new Food(2L, "Crecimiento de gallina ponedora o desarrollo", "Avifort", "50", "Saco", "kg",
                LocalDate.of(2025, 1, 31), "A");

        when(foodRepository.findAllByStatus("A")).thenReturn(Flux.just(food1, food2));

        StepVerifier.create(foodService.getAllActiveFoods())
                .expectNext(food1, food2)
                .verifyComplete();

        verify(foodRepository, times(1)).findAllByStatus("A");
    }

    // 2. Prueba para obtener alimentos por tipo
    @Test
    void testGetFoodsByType() {
        Food food1 = new Food(1L, "Inicio de gallina ponedora", "Dekalb Brown", "50", "Saco", "kg",
                LocalDate.of(2025, 1, 31), "A");

        when(foodRepository.findByFoodType("Inicio de gallina ponedora")).thenReturn(Flux.just(food1));

        StepVerifier.create(foodService.getFoodsByType("Inicio de gallina ponedora"))
                .expectNext(food1)
                .verifyComplete();

        verify(foodRepository, times(1)).findByFoodType("Inicio de gallina ponedora");
    }

    // 3. Prueba para actualizar un alimento existente
    @Test
    void testUpdateFood() {
        Food existingFood = new Food(1L, "Inicio de gallina ponedora", "Dekalb Brown", "50", "Saco", "kg",
                LocalDate.of(2025, 1, 31), "A");
        Food updatedFood = new Food(1L, "Crecimiento de gallina ponedora o desarrollo", "Avifort", "50", "Saco", "kg",
                LocalDate.of(2025, 1, 31), "A");

        when(foodRepository.findById(1L)).thenReturn(Mono.just(existingFood));
        when(foodRepository.save(any(Food.class))).thenReturn(Mono.just(updatedFood));

        StepVerifier.create(foodService.updateFood(1L, new pe.edu.vallegrande.foods.dto.FoodUpdateRequest(
                "Crecimiento de gallina ponedora o desarrollo", "Avifort", "50", "Saco", "kg")))
                .expectNext(updatedFood)
                .verifyComplete();

        verify(foodRepository, times(1)).findById(1L);
        verify(foodRepository, times(1)).save(any(Food.class));
    }

    // 4. Prueba para eliminar lógicamente un alimento
    @Test
    void testDeleteFoodLogically() {
        Food activeFood = new Food(1L, "Inicio de gallina ponedora", "Dekalb Brown", "50", "Saco", "kg",
                LocalDate.of(2025, 1, 31), "A");
        Food inactiveFood = new Food(1L, "Inicio de gallina ponedora", "Dekalb Brown", "50", "Saco", "kg",
                LocalDate.of(2025, 1, 31), "I");

        when(foodRepository.findById(1L)).thenReturn(Mono.just(activeFood));
        when(foodRepository.save(any(Food.class))).thenReturn(Mono.just(inactiveFood));

        StepVerifier.create(foodService.deleteFoodLogically(1L))
                .expectNext(inactiveFood)
                .verifyComplete();

        verify(foodRepository, times(1)).findById(1L);
        verify(foodRepository, times(1)).save(any(Food.class));
    }
}
