package Backend.ms_clasificator.Mappers.MedicalImageTypeMappers;


import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.MedicalImageType;
import org.springframework.stereotype.Component;

@Component
public class MedicalImageTypeMapper implements Mapper<MedicalImageType, MedicalImageTypeCreateDTO, MedicalImageTypeResponseDTO> {
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
                .evaluationAreaId(medicalImageType.getEvaluationArea() != null ? medicalImageType.getEvaluationArea().getId() : null)
                .build();
    }

    @Override
    public MedicalImageTypeResponseDTO toResponseDTO(MedicalImageType medicalImageType) {
        if(medicalImageType == null ){
            return null;
        }
        return MedicalImageTypeResponseDTO.builder()
                .id(medicalImageType.getId())
                .name(medicalImageType.getName())
                .evaluationArea(medicalImageType.getEvaluationArea() != null ? medicalImageType.getEvaluationArea().getName() : null)
                .build();

    }
}
