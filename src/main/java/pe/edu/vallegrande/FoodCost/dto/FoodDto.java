package pe.edu.vallegrande.FoodCost.dto;

import lombok.Data;

/*Dto que consumirá el enpoint del microservicio FOOD*/
@Data
public class FoodDto {
    private String foodType;
    private Integer amount;
}
