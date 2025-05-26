package pe.edu.vallegrande.FoodCost.exception.service;

// Excepción cuando el registro no es encontrado
public class FoodCostNotFoundException extends RuntimeException {
    public FoodCostNotFoundException(String message) {
        super(message);
    }
}
