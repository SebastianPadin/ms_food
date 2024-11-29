package pe.edu.vallegrande.foods.rest;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.vallegrande.foods.model.Food;
import pe.edu.vallegrande.foods.service.FoodService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/foods")
@AllArgsConstructor
public class FoodRest {

    @Autowired
    private FoodService foodService;

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
    public Mono<Food> createFood(@RequestBody Food food) {
        return foodService.createFood(food);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Food>> updateFood(@PathVariable Long id, @RequestBody Food food) {
        return foodService.updateFood(id, food)
                .map(updatedFood -> ResponseEntity.ok(updatedFood))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/delete/{id}")
    public Mono<ResponseEntity<Food>> deleteFoodLogically(@PathVariable Long id) {
        return foodService.deleteFoodLogically(id)
                .map(deletedFood -> ResponseEntity.ok(deletedFood))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/restore/{id}")
    public Mono<ResponseEntity<Food>> restoreFood(@PathVariable Long id) {
        return foodService.restoreFood(id)
                .map(restoredFood -> ResponseEntity.ok(restoredFood))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
