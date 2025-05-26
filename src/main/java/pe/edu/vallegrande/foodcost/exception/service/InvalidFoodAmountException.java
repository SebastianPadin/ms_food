package pe.edu.vallegrande.foodcost.exception.service;

// Excepción cuando la cantidad del alimento es inválida
public class InvalidFoodAmountException extends RuntimeException {
    public InvalidFoodAmountException(String message) {
        super(message);
    }
}
