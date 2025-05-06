/**
 * Paquete que contiene las entidades del modelo de datos.
 */
package pe.edu.vallegrande.foods.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.time.LocalDate;

@Data
@Table("Foods")
public class Food {
    /**
     * Identificador único del alimento.
     */
    @Id
    @Column("id_food")
    private Long idFood;

    /**
     * Tipo de alimento.
     */
    @Column("food_type")
    private String foodType;

    /**
     * Marca del alimento.
     */
    @Column("food_brand")
    private String foodBrand;

    /**
     * Cantidad disponible.
     */
    @Column("amount")
    private Integer amount;

    /**
     * Tipo de empaque del alimento.
     */
    @Column("packaging")
    private String packaging;

    /**
     * Unidad de medida del alimento.
     */
    @Column("unit_measure")
    private String unitMeasure;

    /**
     * Fecha de ingreso del alimento.
     */
    @Column("entry_date")
    private LocalDate entryDate;

    /**
     * Estado del alimento (Activo/Inactivo).
     */
    @Column("status")
    private String status;

}
