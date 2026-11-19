package backend.msclasificator.doctor.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAreaSummaryDTO {

    private Integer id;
    private String codeArea;
    private String name;
    private Integer doctorsCount;

}
