package pe.edu.vallegrande.FoodCost.dto.transfer;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertCostRequestDto {
    private String weekNumber;
    private String foodType;
    private BigDecimal gramsPerChicken;
    private BigDecimal unitPrice;
}
