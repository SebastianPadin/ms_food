package pe.edu.vallegrande.foodcost.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import pe.edu.vallegrande.foodcost.exception.service.FoodCostInactiveException;
import pe.edu.vallegrande.foodcost.exception.service.FoodCostNotFoundException;
import pe.edu.vallegrande.foodcost.exception.service.FoodNotFoundException;
import pe.edu.vallegrande.foodcost.exception.service.InvalidFoodAmountException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FoodCostNotFoundException.class)
    public Mono<Void> handleFoodCostNotFoundException(ServerWebExchange exchange, FoodCostNotFoundException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        return exchange.getResponse().setComplete();
    }

    @ExceptionHandler(FoodCostInactiveException.class)
    public Mono<Void> handleFoodCostInactiveException(ServerWebExchange exchange, FoodCostInactiveException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        return exchange.getResponse().setComplete();
    }

    @ExceptionHandler(FoodNotFoundException.class)
    public Mono<Void> handleFoodNotFoundException(ServerWebExchange exchange, FoodNotFoundException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        return exchange.getResponse().setComplete();
    }

    @ExceptionHandler(InvalidFoodAmountException.class)
    public Mono<Void> handleInvalidFoodAmountException(ServerWebExchange exchange, InvalidFoodAmountException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        return exchange.getResponse().setComplete();
    }
}
