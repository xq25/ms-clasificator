package backend.msclasificator.classification.api.dto;

import backend.msclasificator.dataset.api.dto.DatasetCategorySummaryDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
import backend.msclasificator.doctor.api.dto.DoctorSummaryDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageClassificationResponseDTO {
    private Integer id;
    private DoctorSummaryDTO doctor;
    private MedicalImageSummaryDTO medicalImg;
    private LocalDateTime diagnosticDate;
    private DatasetCategorySummaryDTO datasetCategory;
    // Diagnósticos CIE-10 que definen la categoría (derivados, no elegidos uno a uno)
    private List<MedicalDiagnosticSummaryDTO> diagnostics;
}

