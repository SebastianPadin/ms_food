package pe.edu.vallegrande.FoodCost.dto;

import java.time.LocalDate;

import lombok.Data;
/*Dto que consumirá el enpoint del microservicio HANS*/
@Data
public class HensDto {
    private LocalDate arrivalDate;
    private Integer quantity;    
}
