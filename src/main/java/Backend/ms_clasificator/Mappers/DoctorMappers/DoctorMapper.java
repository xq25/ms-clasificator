package Backend.ms_clasificator.Mappers.DoctorMappers;

import Backend.ms_clasificator.DTOs.Doctor.DoctorCreateDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.Doctor;

public class DoctorMapper implements Mapper<Doctor, DoctorCreateDTO> {
    @Override
    public Doctor toEntity(DoctorCreateDTO doctorCreateDTO) {
        if (doctorCreateDTO == null) {
            return null;
        }

        return Doctor.builder()
                .code(doctorCreateDTO.getCode())
                .user_id(doctorCreateDTO.getUser_id())
                .build();
    }

    @Override
    public DoctorCreateDTO toDTO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        return DoctorCreateDTO.builder()
                .code(doctor.getCode())
                .user_id(doctor.getUser_id())
                .build();
    }
}
