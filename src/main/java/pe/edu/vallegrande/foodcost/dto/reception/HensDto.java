package pe.edu.vallegrande.foodcost.dto.reception;

import java.time.LocalDate;
import lombok.Data;
/*Dto que consumirá el enpoint del microservicio HANS*/
@Data
public class HensDto {
    private Long id;
    private LocalDate arrivalDate;
    private Long shedId;    
}
