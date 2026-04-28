package Backend.ms_clasificator.DTOs.Doctor;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateDoctorDTO {

    @NotBlank(message = "El código es obligatorio")
    private String code;

    @NotNull(message = "El userId es obligatorio")
    private UUID userId;

    @NotNull(message = "El área de evaluación es obligatoria")
    private Integer evaluationAreaId;

}
