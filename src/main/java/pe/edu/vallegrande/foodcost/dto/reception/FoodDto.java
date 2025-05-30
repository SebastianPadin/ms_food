package pe.edu.vallegrande.foodcost.dto.reception;

import lombok.Data;

/*Dto que consumirá el enpoint del microservicio FOOD*/
@Data
public class FoodDto {
    private Long idFood;
    private Integer amount;
}
