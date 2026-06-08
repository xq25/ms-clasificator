package Backend.ms_clasificator.Mappers.DoctorAreaMappers;

import Backend.ms_clasificator.DTOs.DoctorArea.DoctorAreaCreateDTO;
import Backend.ms_clasificator.DTOs.DoctorArea.DoctorAreaResponseDTO;
import Backend.ms_clasificator.DTOs.DoctorArea.DoctorAreaSummaryDTO;
import Backend.ms_clasificator.Mappers.DoctorMappers.DoctorMapper;
import Backend.ms_clasificator.Mappers.EvaluationAreaMappers.EvaluationAreaMapper;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.DoctorArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoctorAreaMapper implements Mapper<DoctorArea, DoctorAreaCreateDTO, DoctorAreaResponseDTO, DoctorAreaSummaryDTO> {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private EvaluationAreaMapper evaluationAreaMapper;

    @Override
    public DoctorArea toEntity(DoctorAreaCreateDTO doctorAreaCreateDTO) {
        if (doctorAreaCreateDTO == null) {
            return null;
        }

        return DoctorArea.builder()
                // Las relaciones se asignan en el Service
                .build();
    }

    @Override
    public DoctorAreaCreateDTO toDTO(DoctorArea doctorArea) {
        if (doctorArea == null) {
            return null;
        }

        return DoctorAreaCreateDTO.builder()
                .doctorId(doctorArea.getDoctor() != null ? doctorArea.getDoctor().getId() : null)
                .evaluationAreaId(doctorArea.getEvaluationArea() != null ? doctorArea.getEvaluationArea().getId() : null)
                .build();
    }

    @Override
    public DoctorAreaResponseDTO toResponseDTO(DoctorArea doctorArea) {
        if (doctorArea == null) {
            return null;
        }

        return DoctorAreaResponseDTO.builder()
                .id(doctorArea.getId())
                .doctor(doctorArea.getDoctor() != null ? doctorMapper.toSummaryDTO(doctorArea.getDoctor()) : null)
                .evaluationArea(doctorArea.getEvaluationArea() != null ? evaluationAreaMapper.toSummaryDTO(doctorArea.getEvaluationArea()) : null)
                .build();
    }

    @Override
    public DoctorAreaSummaryDTO toSummaryDTO(DoctorArea doctorArea) {
        if (doctorArea == null) {
            return null;
        }

        return DoctorAreaSummaryDTO.builder()
                .id(doctorArea.getId())
                .doctorId(doctorArea.getDoctor() != null ? doctorArea.getDoctor().getId() : null)
                .evaluationAreaId(doctorArea.getEvaluationArea() != null ? doctorArea.getEvaluationArea().getId() : null)
                .build();
    }
}