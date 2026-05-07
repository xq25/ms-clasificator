package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Doctor.DoctorBaseDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.DoctorMappers.DoctorMapper;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.DoctorArea;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Repositories.DoctorAreaRepository;
import Backend.ms_clasificator.Repositories.DoctorRepository;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DoctorAreaRepository doctorAreaRepository;

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;

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

    // ===== Relaciones con otras entidades =====

    /**
     * Asociar un doctor a un área de evaluación
     * @param doctorId ID del doctor
     * @param evaluationAreaId ID del área de evaluación
     * @return ApiResponse<DoctorArea> con el resultado de la operación
     */
    public ApiResponse<DoctorArea> joinInEvaluationArea(Integer doctorId, Integer evaluationAreaId) {
        try {
            if (doctorId == null || evaluationAreaId == null) {
                return ApiResponse.error("Los IDs del doctor y del área de evaluación no pueden ser nulos");
            }

            // Validar que exista el doctor
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + doctorId));

            // Validar que exista el área de evaluación
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + evaluationAreaId));

            // Validar que no exista ya esta relación (evitar duplicados por constraint único)
            DoctorArea existing = doctorAreaRepository.findByDoctorIdAndEvaluationAreaId(doctorId, evaluationAreaId);

            if (existing != null) {
                return ApiResponse.error("Este doctor ya está asociado a esta área de evaluación");
            }

            // Crear la relación
            DoctorArea doctorArea = DoctorArea.builder()
                    .doctor(doctor)
                    .evaluationArea(evaluationArea)
                    .build();

            DoctorArea saved = doctorAreaRepository.save(doctorArea);
            return ApiResponse.success(saved, "Doctor asociado al área de evaluación exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al asociar doctor al área de evaluación: " + ex.getMessage());
        }
    }

    /**
     * Eliminar relacion de un doctor de un área de evaluación
     * @param doctorId ID del doctor
     * @param evaluationAreaId ID del área de evaluación
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<Void> removeFromEvaluationArea(Integer doctorId, Integer evaluationAreaId) {
        try {
            DoctorArea doctorArea = doctorAreaRepository.findByDoctorIdAndEvaluationAreaId(doctorId, evaluationAreaId);

            if (doctorArea == null) {
                return ApiResponse.error("No existe relación entre este doctor y esta área de evaluación");
            }

            doctorAreaRepository.delete(doctorArea);
            return ApiResponse.success("Doctor disociado del área de evaluación exitosamente");

        } catch (Exception ex) {
            return ApiResponse.error("Error al disociar doctor del área de evaluación: " + ex.getMessage());
        }
    }
}
