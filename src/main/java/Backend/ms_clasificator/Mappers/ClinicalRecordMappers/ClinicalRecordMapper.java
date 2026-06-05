package Backend.ms_clasificator.Mappers.ClinicalRecordMappers;

import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordCreateDTO;
import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.ClinicalRecord;
import org.springframework.stereotype.Component;

@Component
public class ClinicalRecordMapper implements Mapper<ClinicalRecord, ClinicalRecordCreateDTO, ClinicalRecordResponseDTO> {
    @Override
    public ClinicalRecord toEntity(ClinicalRecordCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return ClinicalRecord.builder()
                .visitDate(dto.getVisitDate())
                .build();
    }

    @Override
    public ClinicalRecordCreateDTO toDTO(ClinicalRecord entity) {
        if (entity == null) {
            return null;
        }

        return ClinicalRecordCreateDTO.builder()
                .visitDate(entity.getVisitDate())
                .patientId(entity.getPatient() != null ? entity.getPatient().getId() : null)
                .build();
    }

    @Override
    public ClinicalRecordResponseDTO toResponseDTO(ClinicalRecord entity) {
        if (entity == null) {
            return null;
        }

        return ClinicalRecordResponseDTO.builder()
                .id(entity.getId())
                .visitDate(entity.getVisitDate())
                .patientInfo(null) // Esta relacion se carga desde el servicio
                .build();
    }
}

