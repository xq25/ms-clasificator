package Backend.ms_clasificator.DTOs.Diagnosis;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisCreateDTO {

    @NotNull(message = "El clinicalRecordId no puede ser nulo")
    private Integer clinicalRecordId;

    @NotNull(message = "El medicalDiagnosticId no puede ser nulo")
    private Integer medicalDiagnosticId;

}

