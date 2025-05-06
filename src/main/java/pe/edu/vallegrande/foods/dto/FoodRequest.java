package pe.edu.vallegrande.foods.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Paquete que contiene los DTOs de Food para insertar y actualizar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodRequest {

    private String foodType;
    private String foodBrand;
    private Integer amount;
    private String packaging;
    private String unitMeasure;
}
