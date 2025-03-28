package pe.edu.vallegrande.FoodCost.repository;

import java.math.BigDecimal;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FoodCostsRepository extends ReactiveCrudRepository<FoodCost, Long> {

    Flux<FoodCost> findAllByStatus(String status);

    Flux<FoodCost> findAllByStatusOrderByIdFoodCostsAsc(String status);

    @Query("CALL insert_food_costs(:weekNumber, :foodId, :gramsPerChicken, :chickensCount, :unitPrice)")
    Mono<Void> insertFoodCost(
            String weekNumber,
            Integer foodId,
            BigDecimal gramsPerChicken,
            Integer chickensCount,
            BigDecimal unitPrice);

    @Query("CALL update_food_cost(:idFoodCosts, :weekNumber, :foodId, :gramsPerChicken, :chickensCount, :unitPrice)")
    Mono<Void> updateFoodCost(
            Integer idFoodCosts,
            String weekNumber,
            Integer foodId,
            BigDecimal gramsPerChicken,
            Integer chickensCount,
            BigDecimal unitPrice);
}