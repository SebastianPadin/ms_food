package pe.edu.vallegrande.FoodCost.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.foods.model.FoodCosts;
import reactor.core.publisher.Flux;

@Repository
public interface FoodCostsRepository extends ReactiveCrudRepository<FoodCosts, Long> {

    Flux<FoodCosts> findAllByStatus(String status);

}