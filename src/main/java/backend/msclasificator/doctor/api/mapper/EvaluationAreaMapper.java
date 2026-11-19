package backend.msclasificator.doctor.api.mapper;

import backend.msclasificator.doctor.api.dto.EvaluationAreaCreateDTO;
import backend.msclasificator.doctor.api.dto.EvaluationAreaResponseDTO;
import backend.msclasificator.doctor.api.dto.EvaluationAreaSummaryDTO;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.doctor.domain.EvaluationArea;
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
                .doctorsCount(evaluationArea.getDoctorsCount())
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
                .doctorsCount(evaluationArea.getDoctorsCount())
                .build();
    }
}