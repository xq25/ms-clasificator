package Backend.ms_clasificator.Mappers.EvaluationAreaMappers;

import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaCreateDTO;
import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaResponseDTO;
import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaSummaryDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.EvaluationArea;
import org.springframework.stereotype.Component;

@Component
public class EvaluationAreaMapper implements Mapper<EvaluationArea, EvaluationAreaCreateDTO, EvaluationAreaResponseDTO, EvaluationAreaSummaryDTO> {

    @Override
    public EvaluationArea toEntity(EvaluationAreaCreateDTO evaluationAreaCreateDTO) {
        if (evaluationAreaCreateDTO == null) {
            return null;
        }

        return EvaluationArea.builder()
                .codeArea(evaluationAreaCreateDTO.getCodeArea())
                .name(evaluationAreaCreateDTO.getName())
                .build();
    }

    @Override
    public EvaluationAreaCreateDTO toDTO(EvaluationArea evaluationArea) {
        if (evaluationArea == null) {
            return null;
        }

        return EvaluationAreaCreateDTO.builder()
                .codeArea(evaluationArea.getCodeArea())
                .name(evaluationArea.getName())
                .build();
    }

    @Override
    public EvaluationAreaResponseDTO toResponseDTO(EvaluationArea evaluationArea) {
        if (evaluationArea == null) {
            return null;
        }

        return EvaluationAreaResponseDTO.builder()
                .id(evaluationArea.getId())
                .name(evaluationArea.getName())
                .codeArea(evaluationArea.getCodeArea())
                .build();
    }

    @Override
    public EvaluationAreaSummaryDTO toSummaryDTO(EvaluationArea evaluationArea) {
        if (evaluationArea == null) {
            return null;
        }

        return EvaluationAreaSummaryDTO.builder()
                .id(evaluationArea.getId())
                .name(evaluationArea.getName())
                .codeArea(evaluationArea.getCodeArea())
                .build();
    }
}