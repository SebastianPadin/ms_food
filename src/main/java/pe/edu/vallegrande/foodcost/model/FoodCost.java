package pe.edu.vallegrande.foodcost.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@Table("food_costs")
public class FoodCost {
    @Id
    @Column("id_food_costs")
    private Long idFoodCosts;

    @Column("week_number")
    private String weekNumber;

    @Column("food_type")
    private String foodType;

    @Column("grams_per_chicken")
    private BigDecimal gramsPerChicken;

    @Column("total_kg")
    private BigDecimal totalKg;

    @Column("total_cost")
    private BigDecimal totalCost;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @Column("shed_name")
    private String shedName;

    @Column("shed_id")
    private Long shedId;

    @Column("hens_id")
    private Long hensId;
    
    @Column("status")
    private String status;

}
