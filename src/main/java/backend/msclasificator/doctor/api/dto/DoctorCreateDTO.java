package backend.msclasificator.doctor.api.dto;

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
public class DoctorCreateDTO {

    @NotBlank(message = "El código del doctor no puede estar vacío")
    private String code;

    @NotBlank(message = "El userId del doctor no puede estar vacío")
    @NotNull(message = "El userId no puede ser nulo")
    private String userId;


}
