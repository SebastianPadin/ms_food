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
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import pe.edu.vallegrande.FoodCost.service.FoodCostsService;
import pe.edu.vallegrande.FoodCost.dto.InsertCostRequestDto;
import pe.edu.vallegrande.FoodCost.dto.UpdateCostRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/food-costs")
public class FoodCostsRest {

    private final FoodCostsService foodCostsService;

    @GetMapping("/actives")
    public Flux<FoodCost> getAllActiveCost() {
        return foodCostsService.getAllActiveCosts();
    }

    @GetMapping("/inactives")
    public Flux<FoodCost> getAllInactiveCost() {
        return foodCostsService.getAllInactiveCosts();
    }

    @PostMapping
    public Mono<String> createFoodCost(@RequestBody InsertCostRequestDto dto) {
        return foodCostsService.addFoodCost(dto)
                .thenReturn("Registro insertado correctamente");
    }

    @PutMapping
    public Mono<String> updateFoodCost(@RequestBody UpdateCostRequestDto dto) {
        return foodCostsService.updateFoodCost(dto)
                .thenReturn("Registro actualizado correctamente");
    }

    @PutMapping("/delete/{id}")
    public Mono<ResponseEntity<FoodCost>> deleteFood(@PathVariable Long id) {
        return foodCostsService.deleteFoodCost(id)
                .map(deletedFood -> ResponseEntity.ok(deletedFood))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/restore/{id}")
    public Mono<ResponseEntity<FoodCost>> restoreFood(@PathVariable Long id) {
        return foodCostsService.restoreFoodCosts(id)
                .map(restoredFood -> ResponseEntity.ok(restoredFood))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}