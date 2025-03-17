package pe.edu.vallegrande.foods.dto;

import lombok.Data;

@Data
public class FoodInsertRequest {
    private String foodType;
    private String foodBrand;
    private String amount;
    private String packaging;
    private String unitMeasure;
    
}
