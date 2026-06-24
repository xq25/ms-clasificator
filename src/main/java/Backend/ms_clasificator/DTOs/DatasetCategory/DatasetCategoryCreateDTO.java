package Backend.ms_clasificator.DTOs.DatasetCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotNull(message = "El nombre de la categoria no puede ser null")
    @NotBlank(message = "El nombre de la categoria no puede ser vacio")
    private String name;

    // EL numValue viene asignado desde las reglas de negocio

    //Esto es una composicion fuerte, no puede existir una categoria sin un dataset, por eso el @NotNull
    @Min(message = "El ID del Dataset a la cual pertence debe ser un numero positivo", value = 1)
    @NotNull(message = "El ID del Dataset a la cual pertence no puede ser null")
    private Integer datasetId;

}
