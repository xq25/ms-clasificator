package Backend.ms_clasificator.DTOs.UIState;

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
public class CreateUIStateDTO {
    //Esto es una composicion fuerte, no puede existir un UIState sin una UIConfig, por eso el @NotNull
    @Min(message = "El ID de la configuracion a la cual pertence debe ser un numero positivo", value = 1)
    @NotNull(message = "El ID de la configuracion a la cual pertence no puede ser null")
    private Integer uiConfigId;

    @Min(value = 1, message = "El ID del diagnostico asociado debe ser un numero positivo")
    @NotNull(message = "El ID del diagnostico relacionado para la clasificacion de este punto no puede ser null")
    private Integer medicalDiagnosticId;
}
