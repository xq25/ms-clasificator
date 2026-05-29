package Backend.ms_clasificator.DTOs.MedicalDiagnostic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalDiagnosticResponseDTO {
    private Integer id;
    private String diagnosticCode;
    private String diagnosticName;
    private Integer parentDiagnosticId;
    private List<Integer> subDiagnosticsIds;
}

