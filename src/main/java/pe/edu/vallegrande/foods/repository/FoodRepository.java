package pe.edu.vallegrande.foods.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.vallegrande.foods.model.Food;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Paquete que contiene el repositorio.
 */
@Repository
public interface FoodRepository extends ReactiveCrudRepository<Food, Long> {

    /**
     * Obtiene alimentos por estado.
     * 
     * @return Lista de alimentos.
     */
    Flux<Food> findAllByStatus(String status);

    /**
     * Obtiene alimentos por campo food_type.
     * 
     * @return Lista de alimento filtrado.
     */
    @Query("SELECT * FROM Foods WHERE LOWER(food_type) LIKE LOWER(CONCAT('%', :foodType, '%')) AND status = 'A'")
    Flux<Food> findByFoodTypeContaining(@Param("foodType") String foodType);
}
