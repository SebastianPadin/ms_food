package pe.edu.vallegrande.FoodCost.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FoodCostsRepository extends ReactiveCrudRepository<FoodCost, Long> {

    Flux<FoodCost> findAllByStatus(String status);

    Flux<FoodCost> findAllByStatusOrderByIdFoodCostsAsc(String status);

    Mono<FoodCost> findTopByOrderByStartDateDesc();

}