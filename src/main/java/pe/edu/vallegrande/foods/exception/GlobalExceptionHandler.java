package pe.edu.vallegrande.foods.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FoodNotFoundException.class)
    public Mono<Void> handleFoodNotFoundException(ServerWebExchange exchange, FoodNotFoundException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        return exchange.getResponse().setComplete();
    }

    @ExceptionHandler(FoodInactiveException.class)
    public Mono<Void> handleFoodInactiveException(ServerWebExchange exchange, FoodInactiveException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        return exchange.getResponse().setComplete();
    }
}
