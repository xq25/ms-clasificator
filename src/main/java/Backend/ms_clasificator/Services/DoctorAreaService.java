package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.DoctorArea.DoctorAreaCreateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.DoctorArea;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Repositories.DoctorAreaRepository;
import Backend.ms_clasificator.Repositories.DoctorRepository;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
import java.util.List;

@Service
@Transactional
public class DoctorAreaService {

    @Autowired
    private DoctorAreaRepository doctorAreaRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;

    /**
     * Obtener todas las relaciones DoctorArea
     * @return Lista de todas las relaciones
     */
    @Transactional(readOnly = true)
    public List<DoctorArea> findAll() {
        return doctorAreaRepository.findAll();
    }

    /**
     * Obtener una relación DoctorArea por ID
     * @param id ID de la relación
     * @return DoctorArea encontrada
     */
    @Transactional(readOnly = true)
    public ApiResponse<DoctorArea> findById(Integer id) {
        try {
            DoctorArea doctorArea = doctorAreaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Relación DoctorArea no encontrada con ID: " + id));

            return ApiResponse.success(doctorArea, "Relacion encontrada exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar relación DoctorArea: " + ex.getMessage());
        }
    }

    /**
     * Obtener las áreas de evaluación de un doctor específico
     * @param doctorId ID del doctor
     * @return Lista de DoctorArea del doctor
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorArea>> findByDoctorId(Integer doctorId) {
        try{
            //Validar que el id del doctor exista
            Doctor doctor = this.doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + doctorId));

            List<DoctorArea> relations = this.doctorAreaRepository.findByDoctorId(doctorId);

            return ApiResponse.success(relations, "Relaciones encontradas exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar relaciones por doctor: " + ex.getMessage());
        }

    }

    /**
     * Obtener los doctores de un área de evaluación específica
     * @param evaluationAreaId ID del área de evaluación
     * @return Lista de DoctorArea en el área
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorArea>> findByEvaluationAreaId(Integer evaluationAreaId) {
        try{
            //Validar que el id del doctor exista
            EvaluationArea evaluationArea= this.evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException("Area de Evaluacion no encontrado con ID: " + evaluationAreaId));

            List<DoctorArea> relations = this.doctorAreaRepository.findByEvaluationAreaId(evaluationAreaId);

            return ApiResponse.success(relations, "Relaciones encontradas exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar relaciones por doctor: " + ex.getMessage());
        }
    }

    /**
     * Crear una nueva relación DoctorArea
     * @param doctorAreaCreateDTO DTO con los datos de la relación
     * @return ApiResponse<DoctorArea> con el resultado de la operación
     */
    public ApiResponse<DoctorArea> create(DoctorAreaCreateDTO doctorAreaCreateDTO) {
        try {
            if (doctorAreaCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            Integer doctorId = doctorAreaCreateDTO.getDoctorId();
            Integer evaluationAreaId = doctorAreaCreateDTO.getEvaluationAreaId();

            // Validar que exista el doctor
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + doctorId));

            // Validar que exista el área de evaluación
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + evaluationAreaId));

            // Validar que no exista ya esta relación
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
            return ApiResponse.error("Error al crear relación DoctorArea: " + ex.getMessage());
        }
    }

    /**
     * Eliminar una relación DoctorArea por ID
     * @param id ID de la relación a eliminar
     * @return ApiResponse<Void> con el resultado de la operación
     */
    public ApiResponse<Void> delete(Integer id) {
        try {
            DoctorArea doctorArea = doctorAreaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Relación DoctorArea no encontrada con ID: " + id));

            doctorAreaRepository.delete(doctorArea);
            return ApiResponse.success("Relación DoctorArea eliminada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar relación DoctorArea: " + ex.getMessage());
        }
    }

    /**
     * Eliminar la relación entre un doctor y un área de evaluación
     * @param doctorId ID del doctor
     * @param evaluationAreaId ID del área de evaluación
     * @return ApiResponse<Void> con el resultado de la operación
     */
    public ApiResponse<Void> deleteByDoctorAndArea(Integer doctorId, Integer evaluationAreaId) {
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

    public boolean validateDoctorInEvaluationArea(Integer doctorId, Integer evaluationAreaId) {
        List<DoctorArea> doctorAreas = this.doctorAreaRepository.findByDoctorId(doctorId);
        return doctorAreas.stream().anyMatch(da -> da.getEvaluationArea().getId().equals(evaluationAreaId));
    }
}

