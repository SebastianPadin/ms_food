package pe.edu.vallegrande.foodcost.webclient.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.foodcost.dto.reception.FoodDto;
import reactor.core.publisher.Mono;

@Component
public class FoodClient {

    private final WebClient webClient;
    private final String foodServiceUrl;

    public FoodClient(@Qualifier("foodWebClient") WebClient webClient,
                      @Value("${api.food-service-url}") String foodServiceUrl) {
        this.webClient = webClient;
        this.foodServiceUrl = foodServiceUrl;
    }

    public Mono<FoodDto> findFoodById(Long foodId) {
        return webClient.get()
                .uri(foodServiceUrl)
                .retrieve()
                .bodyToFlux(FoodDto.class)
                .filter(f -> f.getIdFood().equals(foodId))
                .next()
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontró alimento con ID: " + foodId)));
    }
}
