package Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticCategoryDatasetCreateDTO {
    @NotNull(message = "El ID de la categoria a la que pertenece no puede ser null")
    private Integer datasetCategoryId;

    @NotNull(message = "El ID del diagnostico relacionado para la clasificacion de este punto no puede ser null")
    private Integer medicalDiagnosticId;
}
