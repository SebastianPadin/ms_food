package pe.edu.vallegrande.FoodCost.dto.reception;

import java.time.LocalDate;

import lombok.Data;
/*Dto que consumirá el enpoint del microservicio HANS*/
@Data
public class HensDto {
    
    private Long id;
    private LocalDate arrivalDate;
    private Integer quantity;    
}
