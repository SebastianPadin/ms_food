package pe.edu.vallegrande.FoodCost.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.vallegrande.foods.model.FoodCosts;
import pe.edu.vallegrande.foods.service.FoodCostsService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/food-costs")
@RequiredArgsConstructor
public class FoodCostsRest {

    private final FoodCostsService foodCostsService;

    @GetMapping("actives")
    public Flux<FoodCosts> getAllActiveCost() {
        return foodCostsService.getAllActiveCosts();
    }

    @GetMapping("/inactives")
    public Flux<FoodCosts> getAllInactiveCost() {
        return foodCostsService.getAllInactiveCosts();
    }

    @PostMapping
    public Mono<FoodCosts> createFood(@RequestBody FoodCosts foodcosts) {
        return foodCostsService.createFoodCosts(foodcosts);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<FoodCosts>> updateFoodCosts(@PathVariable Long id, @RequestBody FoodCosts foodCosts) {
        return foodCostsService.updateFoodCosts(id, foodCosts)
                .map(updatedFoodCosts -> ResponseEntity.ok(updatedFoodCosts))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/delete/{id}")
    public Mono<ResponseEntity<FoodCosts>> deleteFood(@PathVariable Long id) {
        return foodCostsService.deleteFoodCost(id)
                .map(deletedFood -> ResponseEntity.ok(deletedFood))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/restore/{id}")
    public Mono<ResponseEntity<FoodCosts>> restoreFood(@PathVariable Long id) {
        return foodCostsService.restoreFoodCosts(id)
                .map(restoredFood -> ResponseEntity.ok(restoredFood))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}