package Backend.ms_clasificator.DTOs.DatasetCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatasetCategoryUpdateDTO {
    @Min(value = 1, message = "El ID del estado UI a actualizar debe ser un numero positivo")
    @NotNull(message = "El ID del diagnostico relacionado para la clasificacion de este punto no puede ser null")
    private Integer numValue;
}
