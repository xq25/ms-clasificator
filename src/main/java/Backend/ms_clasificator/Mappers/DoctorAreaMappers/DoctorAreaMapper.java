package Backend.ms_clasificator.Mappers.DoctorAreaMappers;

import Backend.ms_clasificator.DTOs.DoctorArea.DoctorAreaCreateDTO;
import Backend.ms_clasificator.DTOs.DoctorArea.DoctorAreaResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.DoctorArea;
import org.springframework.stereotype.Component;

@Component
public class DoctorAreaMapper implements Mapper<DoctorArea, DoctorAreaCreateDTO, DoctorAreaResponseDTO> {
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
                .doctorId(doctorArea.getDoctor() != null ? doctorArea.getDoctor().getId() : null)
                .evaluationAreaId(doctorArea.getEvaluationArea() != null ? doctorArea.getEvaluationArea().getId() : null)
                .build();
    }
}

