package pe.edu.vallegrande.foods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("Foods")
public class Food {
    @Id
    private Long id;
    private String foodType;
    private String foodBrand;
    private String unitMeasure;
    private String packaging;  
    private LocalDate entryDate;
    private String status;
}
