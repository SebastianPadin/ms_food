package pe.edu.vallegrande.FoodCost.rest;

import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import pe.edu.vallegrande.FoodCost.service.FoodCostsService;
import pe.edu.vallegrande.FoodCost.service.InsertCostService;
import pe.edu.vallegrande.FoodCost.service.UpdateCostService;
import pe.edu.vallegrande.FoodCost.dto.transfer.FoodCostRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/food-costs")
@AllArgsConstructor
public class FoodCostsRest {

    private final FoodCostsService foodCostsService;
    private final InsertCostService insertCostService;
    private final UpdateCostService updateCostService;


    @GetMapping("/actives")
    public Flux<FoodCost> getAllActiveCost() {
        return foodCostsService.getAllActiveCosts();
    }

    @GetMapping("/inactives")
    public Flux<FoodCost> getAllInactiveCost() {
        return foodCostsService.getAllInactiveCosts();
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