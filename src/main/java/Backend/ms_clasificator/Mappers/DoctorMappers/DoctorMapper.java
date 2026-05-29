package Backend.ms_clasificator.Mappers.DoctorMappers;

import Backend.ms_clasificator.DTOs.Doctor.DoctorBaseDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.Doctor;
import org.springframework.stereotype.Component;

// Esta anotacion es
@Component
public class DoctorMapper implements Mapper<Doctor, DoctorBaseDTO, DoctorResponseDTO> {
    @Override
    public Doctor toEntity(DoctorBaseDTO doctorCreateDTO) {
        if (doctorCreateDTO == null) {
            return null;
        }

        return Doctor.builder()
                .code(doctorCreateDTO.getCode())
                .userId(doctorCreateDTO.getUserId())
                .build();
    }

    @Override
    public DoctorBaseDTO toDTO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        return DoctorBaseDTO.builder()
                .code(doctor.getCode())
                .userId(doctor.getUserId())
                .build();
    }

    @Override
    public DoctorResponseDTO toResponseDTO(Doctor doctor) {
        if (doctor == null) return null;
        return DoctorResponseDTO.builder()
                .id(doctor.getId())
                .code(doctor.getCode())
                .userId(doctor.getUserId())
                .build();
    }
}
