package backend.msclasificator.clinicalrecord.api.dto;

import backend.msclasificator.clinicalrecord.domain.PrimitiveType;
import backend.msclasificator.clinicalrecord.domain.PrimitiveUnit;
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
