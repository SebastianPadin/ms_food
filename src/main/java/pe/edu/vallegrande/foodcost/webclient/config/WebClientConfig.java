package pe.edu.vallegrande.foodcost.webclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.foodcost.exception.client.FoodClientException;
import pe.edu.vallegrande.foodcost.exception.client.HensClientException;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient foodWebClient(WebClient.Builder builder, @Value("${api.food-service-url}") String foodServiceUrl) {
        return builder
                .baseUrl(foodServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(jwtPropagationFilter())                      // Propagación del token
                .filter(errorHandlingFilterForFood())
                .build();
    }

    @Bean
    public WebClient hensWebClient(WebClient.Builder builder, @Value("${api.hens-service-url}") String hensServiceUrl) {
        return builder
                .baseUrl(hensServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(jwtPropagationFilter())                      // Propagación del token
                .filter(errorHandlingFilterForHens())
                .build();
    }

    private ExchangeFilterFunction jwtPropagationFilter() {
        return (request, next) -> Mono.deferContextual(ctx -> {
            if (ctx.hasKey("Authorization")) {
                String token = ctx.get("Authorization");
                return next.exchange(
                        ClientRequest.from(request)
                                .headers(headers -> headers.setBearerAuth(token))
                                .build()
                );
            }
            return next.exchange(request);
        });
    }

    private ExchangeFilterFunction errorHandlingFilterForFood() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(
                                new FoodClientException("Error en Food API: "
                                        + clientResponse.statusCode() + " - " + errorBody)
                        ));
            }
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction errorHandlingFilterForHens() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(
                                new HensClientException("Error en Hens API: "
                                        + clientResponse.statusCode() + " - " + errorBody)
                        ));
            }
            return Mono.just(clientResponse);
        });
    }
}