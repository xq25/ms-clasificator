package backend.msclasificator.diagnosiscatalog.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalDiagnosticResponseDTO {
    private Integer id;
    private String diagnosticCode;
    private String diagnosticName;

    // false si el diagnóstico está retirado del catálogo (soft delete, ADR-013)
    private Boolean active;
    private MedicalDiagnosticSummaryDTO parentDiagnostic;
}

