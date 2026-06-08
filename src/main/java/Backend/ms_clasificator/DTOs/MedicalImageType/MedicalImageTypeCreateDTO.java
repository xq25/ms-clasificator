package Backend.ms_clasificator.DTOs.MedicalImageType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImageTypeCreateDTO {
    @NotBlank(message = "El nombre del tipo de imagen no puede estar vacío")
    private String name;

}
