package backend.msclasificator.clinicalrecord.api.mapper;

import backend.msclasificator.clinicalrecord.api.dto.PatientDatumCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.PatientDatumResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.PatientDatumSummaryDTO;
import backend.msclasificator.clinicalrecord.api.dto.PatientDatumUpdateDTO;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.clinicalrecord.api.mapper.PrimitiveDatumMapper;
import backend.msclasificator.clinicalrecord.domain.PatientDatum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientDatumMapper implements Mapper<PatientDatum, PatientDatumCreateDTO, PatientDatumResponseDTO, PatientDatumSummaryDTO> {

    @Autowired
    private PrimitiveDatumMapper primitiveDatumMapper;

    @Override
    public PatientDatum toEntity(PatientDatumCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return PatientDatum.builder()
                .description(dto.getDescription())
                .build();
    }

    public PatientDatum toEntity(PatientDatumUpdateDTO dto) {
        if (dto == null) {
            return null;
        }

        return PatientDatum.builder()
                .description(dto.getDescription())
                .build();
    }

    @Override
    public PatientDatumCreateDTO toDTO(PatientDatum entity) {
        if (entity == null) {
            return null;
        }

        return PatientDatumCreateDTO.builder()
                .description(entity.getDescription())
                .clinicalRecordId(entity.getClinicalRecord() != null ? entity.getClinicalRecord().getId() : null)
                .primitiveDatumId(entity.getPrimitiveDatum() != null ? entity.getPrimitiveDatum().getId() : null)
                .build();
    }

    @Override
    public PatientDatumResponseDTO toResponseDTO(PatientDatum entity) {
        if (entity == null) {
            return null;
        }

        return PatientDatumResponseDTO.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .primitiveDatum(primitiveDatumMapper.toSummaryDTO(entity.getPrimitiveDatum())) // Lo definimos dentro del service
                .clinicalRecordId(entity.getClinicalRecord().getId())
                .build();
    }

    @Override
    public PatientDatumSummaryDTO toSummaryDTO(PatientDatum entity) {
        if (entity == null) {
            return null;
        }

        return PatientDatumSummaryDTO.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .primitiveDatumId(entity.getPrimitiveDatum().getId())
                .clinicalRecordId(entity.getClinicalRecord().getId())
                .build();
    }
}