package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Doctor.DoctorBaseDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.DoctorMappers.DoctorMapper;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorMapper doctorMapper;

    /**
     * Obtener todos los doctores
     * @return Lista de todos los doctores
     */
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    /**
     * Obtener un doctor por ID
     * @param id ID del doctor
     * @return Doctor encontrado
     * @throws IllegalArgumentException si el doctor no existe
     */
    public Doctor findById(Integer id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + id));
    }

    /**
     * Buscar un doctor por código
     * @param code Código del doctor
     * @return Doctor encontrado o null
     */
    public Doctor findByCode(String code) {
        return doctorRepository.findByCode(code);
    }

    /**
     * Crear un nuevo doctor
     * @param doctorCreateDTO DTO con datos de entrada
     * @return ApiResponse<Doctor> con el resultado de la operación
     */
    public ApiResponse<Doctor> create(DoctorBaseDTO doctorCreateDTO) {
        try {
            if (doctorCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que no exista doctor con el mismo código
            if (doctorRepository.findByCode(doctorCreateDTO.getCode()) != null) {
                return ApiResponse.error("Ya existe un doctor con el código: " + doctorCreateDTO.getCode());
            }

            // Validar que no exista doctor con el mismo userId
            Doctor doctorByUserId = doctorRepository.findByUserId(doctorCreateDTO.getUserId());
            if (doctorByUserId != null) {
                return ApiResponse.error("Ya existe un doctor con el userId: " + doctorCreateDTO.getUserId() + ". El userId debe ser único.");
            }

            Doctor doctor = doctorMapper.toEntity(doctorCreateDTO);
            Doctor saved = doctorRepository.save(doctor);
            return ApiResponse.success(saved, "Doctor creado exitosamente");

        } catch (Exception ex) {
            return ApiResponse.error("Error al crear doctor: " + ex.getMessage());
        }
    }

    /**
     * Actualizar un doctor existente
     * @param id ID del doctor a actualizar
     * @param doctorUpdateDTO DTO con datos a actualizar
     * @return ApiResponse<Doctor> con el resultado de la operación
     */
    public ApiResponse<Doctor> update(Integer id, DoctorUpdateDTO doctorUpdateDTO) {
        try {
            if (doctorUpdateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            Doctor doctor = doctorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + id));

            // Actualizar solo los campos que vienen en el DTO
            // Si están disponibles en DoctorUpdateDTO, se actualizan aquí

            Doctor updated = doctorRepository.save(doctor);
            return ApiResponse.success(updated, "Doctor actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar doctor: " + ex.getMessage());
        }
    }

    /**
     * Eliminar un doctor por ID
     * @param id ID del doctor a eliminar
     * @throws IllegalArgumentException si el doctor no existe
     */
    public void delete(Integer id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + id));

        doctorRepository.delete(doctor);
    }


}
