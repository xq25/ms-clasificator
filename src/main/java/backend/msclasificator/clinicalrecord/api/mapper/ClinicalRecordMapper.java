package backend.msclasificator.clinicalrecord.api.mapper;

import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordSummaryDTO;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
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