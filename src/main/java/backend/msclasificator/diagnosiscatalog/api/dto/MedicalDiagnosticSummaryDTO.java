package backend.msclasificator.diagnosiscatalog.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDiagnosticSummaryDTO {

    private Integer id;
    private String diagnosticCode;
    private String diagnosticName;

    // false si el diagnóstico está retirado del catálogo (soft delete, ADR-013)
    private Boolean active;
}
