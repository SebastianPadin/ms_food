package pe.edu.vallegrande.foods.exception;

public class FoodInactiveException extends RuntimeException {
    public FoodInactiveException(String message) {
        super(message);
    }
}
