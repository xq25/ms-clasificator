package Backend.ms_clasificator.DTOs.MedicalImageType;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MedicalImageTypeUpdateDTO{
    @NotBlank(message = "El nombre del tipo de imagen no puede estar vacío")
    private String name;

}

