package Backend.ms_clasificator.DTOs.ImageDiagnostic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDiagnosticCreateDTO {

    @NotNull(message = "El ID del doctor no puede ser nulo")
    private Integer doctorId;

    @NotNull(message = "El ID de la imagen médica no puede ser nulo")
    private Integer medicalImgId;

    @NotNull(message = "El ID del diagnóstico médico no puede ser nulo")
    private Integer medicalDiagnosticId;

    private LocalDateTime diagnosticDate;
}

