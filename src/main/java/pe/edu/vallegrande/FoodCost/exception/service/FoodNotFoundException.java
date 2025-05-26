package pe.edu.vallegrande.FoodCost.exception.service;

// Excepción cuando el alimento no es encontrado
public class FoodNotFoundException extends RuntimeException {
    public FoodNotFoundException(String message) {
        super(message);
    }
}
