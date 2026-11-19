package backend.msclasificator.clinicalrecord.api.mapper;

import backend.msclasificator.clinicalrecord.api.dto.PrimitiveDatumCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.PrimitiveDatumResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.PrimitiveDatumSummaryDTO;
import backend.msclasificator.clinicalrecord.api.dto.PrimitiveDatumUpdateDTO;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.clinicalrecord.domain.PrimitiveDatum;
import org.springframework.stereotype.Component;

@Component
public class PrimitiveDatumMapper implements Mapper<PrimitiveDatum, PrimitiveDatumCreateDTO, PrimitiveDatumResponseDTO, PrimitiveDatumSummaryDTO> {

    @Override
    public PrimitiveDatum toEntity(PrimitiveDatumCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return PrimitiveDatum.builder()
                .name(dto.getName())
                .type(dto.getType())
                .unit(dto.getUnit())
                .build();
    }

    public PrimitiveDatum toEntity(PrimitiveDatumUpdateDTO dto) {
        if (dto == null) {
            return null;
        }

        return PrimitiveDatum.builder()
                .name(dto.getName())
                .type(dto.getType())
                .unit(dto.getUnit())
                .build();
    }

    @Override
    public PrimitiveDatumCreateDTO toDTO(PrimitiveDatum entity) {
        if (entity == null) {
            return null;
        }

        return PrimitiveDatumCreateDTO.builder()
                .name(entity.getName())
                .type(entity.getType())
                .unit(entity.getUnit())
                .build();
    }

    @Override
    public PrimitiveDatumResponseDTO toResponseDTO(PrimitiveDatum entity) {
        if (entity == null) {
            return null;
        }

        return PrimitiveDatumResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .unit(entity.getUnit())
                .build();
    }

    @Override
    public PrimitiveDatumSummaryDTO toSummaryDTO(PrimitiveDatum entity) {
        if (entity == null) {
            return null;
        }

        return PrimitiveDatumSummaryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .unit(entity.getUnit())
                .build();
    }
}