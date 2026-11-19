package backend.msclasificator.classification.api.mapper;

import backend.msclasificator.classification.api.dto.ClassificationDiagnosisCreateDTO;
import backend.msclasificator.classification.api.dto.ClassificationDiagnosisResponseDTO;
import backend.msclasificator.classification.api.dto.ClassificationDiagnosisSummaryDTO;
import backend.msclasificator.classification.api.mapper.ImageClassificationMapper;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.diagnosiscatalog.api.mapper.MedicalDiagnosticMapper;
import backend.msclasificator.classification.domain.ClassificationDiagnosis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassificationDiagnosisMapper implements Mapper<ClassificationDiagnosis, ClassificationDiagnosisCreateDTO, ClassificationDiagnosisResponseDTO, ClassificationDiagnosisSummaryDTO> {

    @Autowired
    private ImageClassificationMapper imageDiagnosticMapper;

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Override
    public ClassificationDiagnosis toEntity(ClassificationDiagnosisCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return ClassificationDiagnosis.builder()
                .build();
    }

    @Override
    public ClassificationDiagnosisCreateDTO toDTO(ClassificationDiagnosis entity) {
        if (entity == null) {
            return null;
        }

        return ClassificationDiagnosisCreateDTO.builder()
                .imageDiagnosticId(
                        entity.getImageDiagnostic() != null
                                ? entity.getImageDiagnostic().getId()
                                : null
                )
                .medicalDiagnosticId(
                        entity.getMedicalDiagnostic() != null
                                ? entity.getMedicalDiagnostic().getId()
                                : null
                )
                .build();
    }

    @Override
    public ClassificationDiagnosisResponseDTO toResponseDTO(ClassificationDiagnosis entity) {
        if (entity == null) {
            return null;
        }

        return ClassificationDiagnosisResponseDTO.builder()
                .id(entity.getId())
                .imageDiagnostic(entity.getImageDiagnostic() != null ? imageDiagnosticMapper.toSummaryDTO(entity.getImageDiagnostic()) : null)
                .medicalDiagnostic(entity.getMedicalDiagnostic() != null ? medicalDiagnosticMapper.toSummaryDTO(entity.getMedicalDiagnostic()) : null)
                .build();
    }

    @Override
    public ClassificationDiagnosisSummaryDTO toSummaryDTO(ClassificationDiagnosis entity) {
        if (entity == null) {
            return null;
        }

        return ClassificationDiagnosisSummaryDTO.builder()
                .id(entity.getId())
                .imageDiagnosticId(entity.getImageDiagnostic() != null ? entity.getImageDiagnostic().getId() : null)
                .medicalDiagnostic(entity.getMedicalDiagnostic() != null ? medicalDiagnosticMapper.toSummaryDTO(entity.getMedicalDiagnostic()) : null)
                .build();
    }
}