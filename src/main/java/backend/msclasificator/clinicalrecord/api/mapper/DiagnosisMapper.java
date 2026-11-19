package backend.msclasificator.clinicalrecord.api.mapper;

import backend.msclasificator.clinicalrecord.api.dto.DiagnosisCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.DiagnosisResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.DiagnosisSummaryDTO;
import backend.msclasificator.clinicalrecord.api.mapper.ClinicalRecordMapper;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.diagnosiscatalog.api.mapper.MedicalDiagnosticMapper;
import backend.msclasificator.clinicalrecord.domain.Diagnosis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiagnosisMapper implements Mapper<Diagnosis, DiagnosisCreateDTO, DiagnosisResponseDTO, DiagnosisSummaryDTO> {

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Autowired
    private ClinicalRecordMapper clinicalRecordMapper;

    @Override
    public Diagnosis toEntity(DiagnosisCreateDTO dto) {
        if (dto == null) return null;

        return Diagnosis.builder()
                .build();
    }

    @Override
    public DiagnosisCreateDTO toDTO(Diagnosis entity) {
        if (entity == null) return null;

        return DiagnosisCreateDTO.builder()
                .clinicalRecordId(entity.getClinicalRecord() != null ? entity.getClinicalRecord().getId() : null)
                .medicalDiagnosticId(entity.getMedicalDiagnostic() != null ? entity.getMedicalDiagnostic().getId() : null)
                .build();
    }

    @Override
    public DiagnosisResponseDTO toResponseDTO(Diagnosis entity) {
        if (entity == null) return null;

        return DiagnosisResponseDTO.builder()
                .id(entity.getId())
                .medicalDiagnostic(medicalDiagnosticMapper.toSummaryDTO(entity.getMedicalDiagnostic()))
                .clinicalrecord(clinicalRecordMapper.toSummaryDTO(entity.getClinicalRecord()))
                .build();
    }

    @Override
    public DiagnosisSummaryDTO toSummaryDTO(Diagnosis entity) {
        if (entity == null) return null;

        return DiagnosisSummaryDTO.builder()
                .id(entity.getId())
                .medicalDiagnostic(medicalDiagnosticMapper.toSummaryDTO(entity.getMedicalDiagnostic()))
                .build();
    }
}