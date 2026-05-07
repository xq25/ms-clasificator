package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaCreateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.EvaluationAreaMappers.EvaluationAreaMapper;
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
public class EvaluationAreaService {

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;

    @Autowired
    private EvaluationAreaMapper evaluationAreaMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorAreaRepository doctorAreaRepository;

    /**
     * Obtener todas las áreas de evaluación
     * @return Lista de todas las áreas de evaluación
     */
    public List<EvaluationArea> findAll() {
        return evaluationAreaRepository.findAll();
    }

    /**
     * Obtener un área de evaluación por ID
     * @param id ID del área de evaluación
     * @return Área encontrada o null
     */
    public EvaluationArea findById(Integer id) {
        return evaluationAreaRepository.findById(id).orElse(null);
    }

    /**
     * Crear una nueva área de evaluación
     * @param evaluationAreaCreateDTO DTO con datos de entrada
     * @return ApiResponse<EvaluationArea> con el resultado de la operación
     */
    public ApiResponse<EvaluationArea> create(EvaluationAreaCreateDTO evaluationAreaCreateDTO) {
        try {
            if (evaluationAreaCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que no exista área con el mismo nombre
            if (evaluationAreaRepository.findByName(evaluationAreaCreateDTO.getName()) != null) {
                return ApiResponse.error("Ya existe un área de evaluación con el nombre: " + evaluationAreaCreateDTO.getName());
            }

            // Validar que no exista área con el mismo código
            if (evaluationAreaRepository.findByCodeArea(evaluationAreaCreateDTO.getCodeArea()) != null) {
                return ApiResponse.error("Ya existe un área de evaluación con el código: " + evaluationAreaCreateDTO.getCodeArea());
            }

            EvaluationArea evaluationArea = evaluationAreaMapper.toEntity(evaluationAreaCreateDTO);
            EvaluationArea saved = evaluationAreaRepository.save(evaluationArea);
            return ApiResponse.success(saved, "Área de evaluación creada exitosamente");

        } catch (Exception ex) {
            return ApiResponse.error("Error al crear área de evaluación: " + ex.getMessage());
        }
    }

    /**
     * Actualizar un área de evaluación existente
     * @param id ID del área a actualizar
     * @param evaluationAreaCreateDTO DTO con datos a actualizar
     * @return ApiResponse<EvaluationArea> con el resultado de la operación
     */
    public ApiResponse<EvaluationArea> update(Integer id, EvaluationAreaCreateDTO evaluationAreaCreateDTO) {
        try {
            if (evaluationAreaCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            EvaluationArea evaluationArea = evaluationAreaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + id));

            // Validar que no exista otro área con el mismo nombre
            EvaluationArea existingByName = evaluationAreaRepository.findByName(evaluationAreaCreateDTO.getName());
            if (existingByName != null && !existingByName.getId().equals(id)) {
                return ApiResponse.error("Ya existe un área de evaluación con el nombre: " + evaluationAreaCreateDTO.getName());
            }

            // Validar que no exista otro área con el mismo código
            EvaluationArea existingByCode = evaluationAreaRepository.findByCodeArea(evaluationAreaCreateDTO.getCodeArea());
            if (existingByCode != null && !existingByCode.getId().equals(id)) {
                return ApiResponse.error("Ya existe un área de evaluación con el código: " + evaluationAreaCreateDTO.getCodeArea());
            }

            evaluationArea.setCodeArea(evaluationAreaCreateDTO.getCodeArea());
            evaluationArea.setName(evaluationAreaCreateDTO.getName());

            EvaluationArea updated = evaluationAreaRepository.save(evaluationArea);
            return ApiResponse.success(updated, "Área de evaluación actualizada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar área de evaluación: " + ex.getMessage());
        }
    }

    /**
     * Eliminar un área de evaluación por ID
     * @param id ID del área a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<Void> delete(Integer id) {
        try {
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + id));

            evaluationAreaRepository.delete(evaluationArea);
            return ApiResponse.success("Área de evaluación eliminada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar área de evaluación: " + ex.getMessage());
        }
    }

    // ===== Relaciones con otras entidades =====

    /**
     * Agregar un doctor a un área de evaluación
     * @param evaluationAreaId ID del área de evaluación
     * @param doctorId ID del doctor
     * @return ApiResponse<DoctorArea> con el resultado de la operación
     */
    public ApiResponse<DoctorArea> addDoctor(Integer evaluationAreaId, Integer doctorId) {
        try {
            if (evaluationAreaId == null || doctorId == null) {
                return ApiResponse.error("Los IDs del área de evaluación y del doctor no pueden ser nulos");
            }

            // Validar que exista el área de evaluación
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + evaluationAreaId));

            // Validar que exista el doctor
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + doctorId));

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
            return ApiResponse.success(saved, "Doctor agregado al área de evaluación exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al agregar doctor al área de evaluación: " + ex.getMessage());
        }
    }

    /**
     * Remover un doctor de un área de evaluación
     * @param evaluationAreaId ID del área de evaluación
     * @param doctorId ID del doctor
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<Void> removeDoctor(Integer evaluationAreaId, Integer doctorId) {
        try {
            DoctorArea doctorArea = doctorAreaRepository.findByDoctorIdAndEvaluationAreaId(doctorId, evaluationAreaId);

            if (doctorArea == null) {
                return ApiResponse.error("No existe relación entre este doctor y esta área de evaluación");
            }

            doctorAreaRepository.delete(doctorArea);
            return ApiResponse.success("Doctor removido del área de evaluación exitosamente");

        } catch (Exception ex) {
            return ApiResponse.error("Error al remover doctor del área de evaluación: " + ex.getMessage());
        }
    }
}
