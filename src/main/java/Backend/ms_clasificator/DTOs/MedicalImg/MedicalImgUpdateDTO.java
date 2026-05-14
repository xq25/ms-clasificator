package Backend.ms_clasificator.DTOs.MedicalImg;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImgUpdateDTO {

    @NotNull(message = "La URL de la imagen médica no puede ser nulo")
    @NotBlank(message = "La URL de la imagen médica no puede estar vacía")
    private String url;

}

