package Backend.ms_clasificator.DTOs.UIState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIStateResponseDTO {
    private Integer id;
    private Integer uiConfigId;
    private Integer medicalDiagnosticId;
}

