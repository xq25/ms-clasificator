package backend.msclasificator.classification.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationDiagnosisCreateDTO {
    @NotNull(message = "el id del diagnostico de la imagen no puede ser nulo")
    private Integer imageDiagnosticId;

    @NotNull(message = "el id del diagnostico medico no puede ser nulo")
    private Integer medicalDiagnosticId;
}
