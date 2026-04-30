package Backend.ms_clasificator.Mappers.DoctorMappers;

import Backend.ms_clasificator.DTOs.Doctor.DoctorCreateDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.Doctor;

public class DoctorMapper implements Mapper<Doctor, DoctorCreateDTO> {
    @Override
    public Doctor toEntity(DoctorCreateDTO doctorCreateDTO) {
        return null;
    }

    @Override
    public DoctorCreateDTO toDTO(Doctor doctor) {
        return null;
    }
}
