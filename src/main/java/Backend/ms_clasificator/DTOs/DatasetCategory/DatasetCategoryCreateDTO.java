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
public class DatasetCategoryCreateDTO {
    // EL numValue viene asignado desde las reglas de negocio

    //Esto es una composicion fuerte, no puede existir un UIState sin una UIConfig, por eso el @NotNull
    @Min(message = "El ID del Dataset a la cual pertence debe ser un numero positivo", value = 1)
    @NotNull(message = "El ID del Dataset a la cual pertence no puede ser null")
    private Integer datasetId;

}
