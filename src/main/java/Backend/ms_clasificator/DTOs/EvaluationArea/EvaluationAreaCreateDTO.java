package Backend.ms_clasificator.DTOs.EvaluationArea;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationAreaCreateDTO {

    @Max(10)
    @NotBlank(message = "El código del área de evaluación no puede estar vacío")
    private String codeArea;

    @NotBlank(message = "El nombre del área de evaluación no puede estar vacío")
    private String name;
}

