package Backend.ms_clasificator.DTOs.DatasetCategory;

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
public class DatasetCategoryUpdateDTO {

    @NotBlank(message = "El nombre de la categoria no puede ser vacio")
    @NotNull(message = "El nombre de la categoria no puede ser null")
    private String name;
}
