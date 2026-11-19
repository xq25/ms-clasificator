package backend.msclasificator.clinicalrecord.api.dto;

import backend.msclasificator.clinicalrecord.domain.PrimitiveType;
import backend.msclasificator.clinicalrecord.domain.PrimitiveUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PrimitiveDatumUpdateDTO {
    @NotNull(message = "El nombre del dato primitivo no puede ser nulo")
    @NotBlank(message = "El nombre del dato primitivo no puede estar vacío")
	private String name;

    @NotNull(message = "El tipo del dato primitivo no puede ser nulo")
	private PrimitiveType type;

    @NotNull(message = "La unidad del dato primitivo no puede ser nula")
	private PrimitiveUnit unit;
}
