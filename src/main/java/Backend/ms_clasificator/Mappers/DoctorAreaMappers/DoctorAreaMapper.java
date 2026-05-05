package Backend.ms_clasificator.Mappers.DoctorAreaMappers;

import Backend.ms_clasificator.DTOs.DoctorArea.DoctorAreaCreateDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.DoctorArea;

public class DoctorAreaMapper implements Mapper<DoctorArea, DoctorAreaCreateDTO> {
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
}

