package Backend.ms_clasificator.Mappers.PrimitiveDatumMappers;

import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumCreateDTO;
import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumResponseDTO;
import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumSummaryDTO;
import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumUpdateDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.PrimitiveDatum;
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