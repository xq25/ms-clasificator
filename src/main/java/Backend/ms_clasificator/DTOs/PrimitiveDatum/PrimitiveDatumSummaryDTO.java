package Backend.ms_clasificator.DTOs.PrimitiveDatum;

import Backend.ms_clasificator.Models.PrimitiveType;
import Backend.ms_clasificator.Models.PrimitiveUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrimitiveDatumSummaryDTO {

    private Integer id;
    private String name;
    private PrimitiveType type;
    private PrimitiveUnit unit;

}
