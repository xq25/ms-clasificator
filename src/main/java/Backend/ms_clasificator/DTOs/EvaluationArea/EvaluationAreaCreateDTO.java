package Backend.ms_clasificator.DTOs.EvaluationArea;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 10, message = "El código del área debe tener máximo 10 caracteres")
    @NotBlank(message = "El código del área de evaluación no puede estar vacío")
    @NotNull(message = "El código del área de evaluación no puede ser nulo")
    private String codeArea;

    @NotBlank(message = "El nombre del área de evaluación no puede estar vacío")
    @NotNull(message = "El nombre del área de evaluación no puede ser nulo")
    private String name;
}

