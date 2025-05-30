package pe.edu.vallegrande.foods.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.time.LocalDate;

@Data
@Table("Foods")
public class Food {
    @Id
    @Column("id_food")
    private Long idFood;

    @Column("food_type")
    private String foodType;

    @Column("food_brand")
    private String foodBrand;

    @Column("amount")
    private Integer amount;

    @Column("packaging")
    private String packaging;

    @Column("unit_measure")
    private String unitMeasure;

    @Column("entry_date")
    private LocalDate entryDate;

    @Column("status")
    private String status;
}
