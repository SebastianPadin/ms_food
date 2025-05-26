package pe.edu.vallegrande.foodcost.exception.service;

// Excepción cuando el registro ya está inactivo
public class FoodCostInactiveException extends RuntimeException {
    public FoodCostInactiveException(String message) {
        super(message);
    }
}