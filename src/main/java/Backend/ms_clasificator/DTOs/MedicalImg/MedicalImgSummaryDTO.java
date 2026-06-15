package Backend.ms_clasificator.DTOs.MedicalImg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImgSummaryDTO {

    private Integer id;
    private String imageKey;
    private String provider;
    private String contentType;
    private String medicalImageTypeName;
    private Long fileSize;
    private LocalDateTime createdAt;

}
