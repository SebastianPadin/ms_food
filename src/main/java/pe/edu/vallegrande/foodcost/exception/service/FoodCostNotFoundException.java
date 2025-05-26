package pe.edu.vallegrande.foodcost.exception.service;

// Excepción cuando el registro no es encontrado
public class FoodCostNotFoundException extends RuntimeException {
    public FoodCostNotFoundException(String message) {
        super(message);
    }
}
