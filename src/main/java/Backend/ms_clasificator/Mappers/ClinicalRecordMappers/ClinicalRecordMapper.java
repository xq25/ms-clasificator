package Backend.ms_clasificator.Mappers.ClinicalRecordMappers;

import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordCreateDTO;
import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordResponseDTO;
import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordSummaryDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.ClinicalRecord;
import org.springframework.stereotype.Component;

@Component
public class ClinicalRecordMapper implements Mapper<ClinicalRecord, ClinicalRecordCreateDTO, ClinicalRecordResponseDTO, ClinicalRecordSummaryDTO> {

    @Override
    public ClinicalRecord toEntity(ClinicalRecordCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return ClinicalRecord.builder()
                .chiefComplaint(dto.getChiefComplaint())
                .visitDate(dto.getVisitDate())
                .build();
    }

    @Override
    public ClinicalRecordCreateDTO toDTO(ClinicalRecord entity) {
        if (entity == null) {
            return null;
        }

        return ClinicalRecordCreateDTO.builder()
                .chiefComplaint(entity.getChiefComplaint())
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
                .chiefComplaint(entity.getChiefComplaint())
                .visitDate(entity.getVisitDate())
                .patientId(entity.getPatient() != null ? entity.getPatient().getId() : null)
                .patientDocument(entity.getPatient() != null ? entity.getPatient().getDocument() : null)
                .build();
    }

    @Override
    public ClinicalRecordSummaryDTO toSummaryDTO(ClinicalRecord entity) {
        if (entity == null) {
            return null;
        }

        return ClinicalRecordSummaryDTO.builder()
                .id(entity.getId())
                .chiefComplaint(entity.getChiefComplaint())
                .visitDate(entity.getVisitDate())
                .build();
    }
}