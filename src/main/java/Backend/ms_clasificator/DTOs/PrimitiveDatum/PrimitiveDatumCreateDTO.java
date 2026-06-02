package Backend.ms_clasificator.DTOs.PrimitiveDatum;

import Backend.ms_clasificator.Models.PrimitiveType;
import Backend.ms_clasificator.Models.PrimitiveUnit;
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
public class PrimitiveDatumCreateDTO {

    @NotNull(message = "El nombre no puede ser nulo")
	@NotBlank(message = "El nombre no puede estar vacío")
	private String name;

	@NotNull(message = "El tipo no puede ser nulo")
	private PrimitiveType type;

	@NotNull(message = "La unidad no puede ser nula")
	private PrimitiveUnit unit;
}
