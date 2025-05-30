package pe.edu.vallegrande.foodcost.controller;

import lombok.AllArgsConstructor;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.foodcost.model.FoodCost;
import pe.edu.vallegrande.foodcost.service.FoodCostsService;
import pe.edu.vallegrande.foodcost.service.UpdateCostService;
import pe.edu.vallegrande.foodcost.service.InsertCostService;
import pe.edu.vallegrande.foodcost.dto.transfer.FoodCostRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/food-costs")
@AllArgsConstructor
public class FoodCostsController {

    private final FoodCostsService foodCostsService;
    private final UpdateCostService updateCostService;
    private final InsertCostService insertCostService;


    @GetMapping("/actives")
    public Flux<FoodCost> getAllActiveCost() {
        return foodCostsService.getAllActiveCosts();
    }

    @GetMapping("/inactives")
    public Flux<FoodCost> getAllInactiveCost() {
        return foodCostsService.getAllInactiveCosts();
    }

    @GetMapping("/search/{weekNumber}")
    public Flux<FoodCost> getByWeekNumber(@PathVariable String weekNumber) {
        return foodCostsService.getByWeekNumber(weekNumber);
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, String>>> createFoodCost(@RequestBody FoodCostRequestDto dto) {
        return insertCostService.addFoodCost(dto)
                .thenReturn(ResponseEntity.ok(Collections.singletonMap("message", "Registro insertado correctamente")));
    }

    @PutMapping("/{idFoodCosts}")
    public Mono<ResponseEntity<Map<String, String>>> updateFoodCost(
            @PathVariable Long idFoodCosts,
            @RequestBody FoodCostRequestDto dto) {

        return updateCostService.updateFoodCost(idFoodCosts, dto)
                .thenReturn(
                        ResponseEntity.ok(Collections.singletonMap("message", "Registro actualizado correctamente")));
    }

    @PutMapping("/delete/{id}")
    public Mono<ResponseEntity<FoodCost>> deleteFood(@PathVariable Long id) {
        return foodCostsService.deleteFoodCost(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/restore/{id}")
    public Mono<ResponseEntity<FoodCost>> restoreFood(@PathVariable Long id) {
        return foodCostsService.restoreFoodCosts(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/physical/{id}")
    public Mono<ResponseEntity<Void>> deleteFoodCostPhysically(@PathVariable Long id) {
        return foodCostsService.deleteFoodCostPhysically(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}