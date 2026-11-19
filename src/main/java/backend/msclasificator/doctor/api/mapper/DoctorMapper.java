package backend.msclasificator.doctor.api.mapper;

import backend.msclasificator.doctor.api.dto.DoctorCreateDTO;
import backend.msclasificator.doctor.api.dto.DoctorResponseDTO;
import backend.msclasificator.doctor.api.dto.DoctorSummaryDTO;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.doctor.domain.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper implements Mapper<Doctor, DoctorCreateDTO, DoctorResponseDTO, DoctorSummaryDTO> {

    @Override
    public Doctor toEntity(DoctorCreateDTO doctorCreateDTO) {
        if (doctorCreateDTO == null) {
            return null;
        }

        return Doctor.builder()
                .code(doctorCreateDTO.getCode())
                .userId(doctorCreateDTO.getUserId())
                .build();
    }

    @Override
    public DoctorCreateDTO toDTO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        return DoctorCreateDTO.builder()
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
                .userInfo(null) // Se asigna en el service
                .build();
    }

    @Override
    public DoctorSummaryDTO toSummaryDTO(Doctor doctor) {
        if (doctor == null) return null;

        return DoctorSummaryDTO.builder()
                .id(doctor.getId())
                .code(doctor.getCode())
                .userId(doctor.getUserId())
                .build();
    }
}