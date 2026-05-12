package Backend.ms_clasificator.DTOs.UIState;

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
    @NotNull(message = "El ID de la configuracion a la cual pertence no puede ser null")
    private Integer iuConfigId;

    @NotNull(message = "El ID del diagnostico relacionado para la clasificacion de este punto no puede ser null")
    private Integer medicalDiagnosticId;
}
