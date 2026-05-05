package Backend.ms_clasificator.DTOs.DoctorArea;

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
    private Integer doctorId;

    @NotNull(message = "El ID del área de evaluación no puede ser nulo")
    private Integer evaluationAreaId;
}

