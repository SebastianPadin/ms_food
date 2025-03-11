package pe.edu.vallegrande.FoodCost.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Table("food_costs")
public class FoodCost {
    @Id
    private Long idFoodCosts;
    private String weekNumber;
    private Integer foodId;
    private BigDecimal gramsPerChicken;
    private BigDecimal totalKg;
    private BigDecimal totalCost;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}