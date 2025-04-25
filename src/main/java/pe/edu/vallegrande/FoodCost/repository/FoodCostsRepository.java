package pe.edu.vallegrande.FoodCost.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FoodCostsRepository extends ReactiveCrudRepository<FoodCost, Long> {

    @Query("SELECT * FROM food_costs WHERE LOWER(week_number) LIKE LOWER(CONCAT('%', :weekNumber, '%'))")
    Flux<FoodCost> findByWeekNumber(@Param("weekNumber") String weekNumber);

    Flux<FoodCost> findAllByStatusOrderByIdFoodCostsAsc(String status);

    Mono<FoodCost> findTopByOrderByStartDateDesc();

}