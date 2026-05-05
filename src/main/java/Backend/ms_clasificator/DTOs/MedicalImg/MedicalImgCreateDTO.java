package Backend.ms_clasificator.DTOs.MedicalImg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImgCreateDTO {

    @NotBlank(message = "La URL de la imagen no puede estar vacía")
    private String url;

    @NotNull(message = "El ID del área de evaluación no puede ser nulo")
    private Integer evaluationAreaId;
    
    // Opcional: la imagen puede crearse sin paciente asignado
    // Se asigna después si es necesario
    private Integer patientId;
}

