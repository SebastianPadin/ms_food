package pe.edu.vallegrande.foodcost.dto.transfer;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodCostRequestDto {
    private String weekNumber;
    private String foodType;
    private BigDecimal gramsPerChicken;
    private BigDecimal unitPrice;
    private String shedName;
    private Integer quantity;
    private Long foodId;
    private Long hensId;
}
