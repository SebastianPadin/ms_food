package pe.edu.vallegrande.FoodCost.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import reactor.core.publisher.Flux;

@Repository
public interface FoodCostsRepository extends ReactiveCrudRepository<FoodCost, Long> {

    Flux<FoodCost> findAllByStatus(String status);

}