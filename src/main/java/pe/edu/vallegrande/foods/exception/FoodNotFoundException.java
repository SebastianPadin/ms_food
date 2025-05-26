package pe.edu.vallegrande.foods.exception;

public class FoodNotFoundException extends RuntimeException {
    public FoodNotFoundException(String message) {
        super(message);
    }
}