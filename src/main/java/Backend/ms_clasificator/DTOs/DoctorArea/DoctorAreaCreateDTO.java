package Backend.ms_clasificator.DTOs.DoctorArea;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAreaCreateDTO {

    @NotNull(message = "El ID del doctor no puede ser nulo")
    @Min(value = 1, message = "El ID del doctor debe ser un número positivo")
    private Integer doctorId;

    @NotNull(message = "El ID del área de evaluación no puede ser nulo")
    @Min(value = 1, message = "El ID del área de evaluación debe ser un número positivo")
    private Integer evaluationAreaId;
}

