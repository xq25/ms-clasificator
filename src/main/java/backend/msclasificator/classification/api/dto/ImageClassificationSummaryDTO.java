package backend.msclasificator.classification.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageClassificationSummaryDTO {

    private Integer id;
    private Integer doctorId;
    private Integer medicalImageId;
    private LocalDateTime diagnosticDate;

}
