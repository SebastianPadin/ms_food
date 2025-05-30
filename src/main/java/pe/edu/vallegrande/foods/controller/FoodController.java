package pe.edu.vallegrande.foods.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.foods.model.Food;
import pe.edu.vallegrande.foods.dto.FoodRequest;
import pe.edu.vallegrande.foods.service.FoodService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/foods")
@AllArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping
    public Flux<Food> getAllFoods() {
        return foodService.getAllFoods();
    }

    @GetMapping("/actives")
    public Flux<Food> getAllActiveFoods() {
        return foodService.getAllActiveFoods();
    }

    @GetMapping("/inactives")
    public Flux<Food> getAllInactiveFoods() {
        return foodService.getAllInactiveFoods();
    }

    @GetMapping("/type/{foodType}")
    public Flux<Food> getFoodsByType(@PathVariable String foodType) {
        return foodService.getFoodsByType(foodType);
    }

    @PostMapping
    public Mono<ResponseEntity<Food>> createFood(@RequestBody FoodRequest request) {
        return foodService.createFood(request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Food>> updateFood(@PathVariable Long id, @RequestBody FoodRequest foodRequest) {
        return foodService.updateFood(id, foodRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/delete/{id}")
    public Mono<ResponseEntity<Food>> deleteFoodLogically(@PathVariable Long id) {
        return foodService.deleteFoodLogically(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/restore/{id}")
    public Mono<ResponseEntity<Food>> restoreFood(@PathVariable Long id) {
        return foodService.restoreFood(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/physical/{id}")
    public Mono<ResponseEntity<Void>> deleteFoodPhysically(@PathVariable Long id) {
        return foodService.deleteFoodPhysically(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
