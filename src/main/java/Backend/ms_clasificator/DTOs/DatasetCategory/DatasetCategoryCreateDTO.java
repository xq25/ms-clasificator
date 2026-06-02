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
    //Esto es una composicion fuerte, no puede existir un UIState sin una UIConfig, por eso el @NotNull
    @Min(message = "El ID del Dataset a la cual pertence debe ser un numero positivo", value = 1)
    @NotNull(message = "El ID del Dataset a la cual pertence no puede ser null")
    private Integer datasetId;

    @NotNull(message = "Se debe especificar el numero de categoria al que corresponde")
    @Min(message = "El valor numerico de la posicion de la categoria tiene que se mayor que 0", value = 1)
    private Integer numValue;


}
