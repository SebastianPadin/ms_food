
/**
 * Paquete que contiene los controladores de la aplicación.
 */
package pe.edu.vallegrande.foods.controller;

import lombok.AllArgsConstructor;
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
import pe.edu.vallegrande.foods.dto.FoodRequest;
import pe.edu.vallegrande.foods.service.FoodService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/foods")
@AllArgsConstructor
public final class FoodController {

    /**
     * Servicio que gestiona las operaciones sobre los alimentos.
     */
    private final FoodService foodService;

    /**
     * Obtiene todos los alimentos disponibles en la base de datos.
     *
     * @return Lista de alimentos.
     */
    @GetMapping
    public Flux<Food> getAllFoods() {
        return foodService.getAllFoods();
    }

    /**
     * Obtiene todos los alimentos activos.
     *
     * @return Lista de alimentos activos.
     */
    @GetMapping("/actives")
    public Flux<Food> getAllActiveFoods() {
        return foodService.getAllActiveFoods();
    }

    /**
     * Obtiene todos los alimentos inactivos.
     *
     * @return Lista de alimentos inactivos.
     */
    @GetMapping("/inactives")
    public Flux<Food> getAllInactiveFoods() {
        return foodService.getAllInactiveFoods();
    }

    /**
     * Obtiene el alimento filtrado por tipo.
     *
     * @param foodType Tipo de alimento
     * @return Lista de alimentos filtrados.
     */
    @GetMapping("/type/{foodType}")
    public Flux<Food> getFoodsByType(@PathVariable final String foodType) {
        return foodService.getFoodsByType(foodType);
    }

    /**
     * Inserta un nuevo alimento.
     *
     * @param request Dto que insertará el nuevo alimento
     * @return Alimento insertado.
     */
    @PostMapping
    public Mono<ResponseEntity<Food>> createFood(@RequestBody final FoodRequest request) {
        return foodService.createFood(request)
            .map(food -> ResponseEntity.ok(food))
            .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    /**
     * Actualiza un nuevo alimento.
     *
     * @param id Identificador del alimento
     * @param request Dto que actualizará el alimento
     * @return Alimento actualizado.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Food>> updateFood(@PathVariable final Long id, @RequestBody final FoodRequest foodRequest) {
        return foodService.updateFood(id, foodRequest)
            .map(updatedFood -> ResponseEntity.ok(updatedFood))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un alimento de manera lógica.
     *
     * @param id Identificador del alimento 
     * @return Alimento eliminado.
     */
    @PutMapping("/delete/{id}")
    public Mono<ResponseEntity<Food>> deleteFoodLogically(@PathVariable final Long id) {
        return foodService.deleteFoodLogically(id)
            .map(deletedFood -> ResponseEntity.ok(deletedFood))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Reestaura un alimento previamente eliminado.
     *
     * @param id Identificador del alimento
     * @return Alimento restaurado.
     */
    @PutMapping("/restore/{id}")
    public Mono<ResponseEntity<Food>> restoreFood(@PathVariable final Long id) {
        return foodService.restoreFood(id)
            .map(restoredFood -> ResponseEntity.ok(restoredFood))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
