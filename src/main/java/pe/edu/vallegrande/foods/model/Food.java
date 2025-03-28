package pe.edu.vallegrande.foods.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;

@Data
@Table("Foods")
public class Food {
    @Id
    private Long id_food;
    private String foodType;
    private String foodBrand;
    private Integer amount;
    private String packaging;
    private String unitMeasure;  
    private LocalDate entryDate;
    private String status;
}
