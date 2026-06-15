package Backend.ms_clasificator.DTOs.EvaluationArea;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationAreaResponseDTO {
    private Integer id;
    private String codeArea;
    private String name;
    private Integer doctorsCount;
}

