package pe.edu.vallegrande.FoodCost.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertCostRequestDto {
    private String weekNumber;
    private Integer foodId;
    private BigDecimal gramsPerChicken;
    private Integer chickensCount;
    private BigDecimal unitPrice;
}
