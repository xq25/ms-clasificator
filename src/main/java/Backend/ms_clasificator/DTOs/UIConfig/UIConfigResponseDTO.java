package Backend.ms_clasificator.DTOs.UIConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIConfigResponseDTO {
    private Integer id;
    private Integer medicalDiagnosticId;
    private Integer evaluationAreaId;
}

