/**
 * Paquete que contiene los Data Transfer Objects (DTOs) de la aplicación.
 */
package pe.edu.vallegrande.foods.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodRequest {

    /**
     * Tipo de alimento.
     */
    private String foodType;

    /**
     * Marca del alimento.
     */
    private String foodBrand;

    /**
     * Cantidad disponible.
     */
    private Integer amount;

    /**
     * Tipo de empaque del alimento.
     */
    private String packaging;

    /**
     * Unidad de medida del alimento.
     */
    private String unitMeasure;
}
