package backend.msclasificator.medicalimage.api.mapper;

import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeCreateDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeResponseDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeSummaryDTO;
import backend.msclasificator.doctor.api.mapper.EvaluationAreaMapper;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.medicalimage.domain.MedicalImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicalImageTypeMapper implements Mapper<MedicalImageType, MedicalImageTypeCreateDTO, MedicalImageTypeResponseDTO, MedicalImageTypeSummaryDTO> {

    @Autowired
    private EvaluationAreaMapper evaluationAreaMapper;

    @Override
    public MedicalImageType toEntity(MedicalImageTypeCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return MedicalImageType.builder()
                .name(dto.getName())
                .evaluationArea(null)
                .build();
    }

    @Override
    public MedicalImageTypeCreateDTO toDTO(MedicalImageType medicalImageType) {
        if (medicalImageType == null) {
            return null;
        }

        return MedicalImageTypeCreateDTO.builder()
                .name(medicalImageType.getName())
                .build();
    }

    @Override
    public MedicalImageTypeResponseDTO toResponseDTO(MedicalImageType medicalImageType) {
        if (medicalImageType == null) {
            return null;
        }

        return MedicalImageTypeResponseDTO.builder()
                .id(medicalImageType.getId())
                .name(medicalImageType.getName())
                .active(medicalImageType.getActive())
                .evaluationArea(medicalImageType.getEvaluationArea() != null ? evaluationAreaMapper.toSummaryDTO(medicalImageType.getEvaluationArea()) : null)
                .build();
    }

    @Override
    public MedicalImageTypeSummaryDTO toSummaryDTO(MedicalImageType medicalImageType) {
        if (medicalImageType == null) {
            return null;
        }

        return MedicalImageTypeSummaryDTO.builder()
                .id(medicalImageType.getId())
                .name(medicalImageType.getName())
                .active(medicalImageType.getActive())
                .build();
    }
}