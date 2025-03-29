package pe.edu.vallegrande.foods.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodInsertRequest {

    private String foodType;
    private String foodBrand;
    private Integer amount;
    private String packaging;
    private String unitMeasure;
}
