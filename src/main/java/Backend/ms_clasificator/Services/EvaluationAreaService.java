package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaCreateDTO;
import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.EvaluationAreaMappers.EvaluationAreaMapper;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EvaluationAreaService {

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;

    @Autowired
    private EvaluationAreaMapper evaluationAreaMapper;


    /**
     * Obtener todas las áreas de evaluación
     * @return Lista de todas las áreas de evaluación
     */
    @Transactional(readOnly = true)
    public List<EvaluationArea> findAll() {
        return evaluationAreaRepository.findAll();
    }

    /**
     * Obtener un área de evaluación por ID
     * @param id ID del área de evaluación
     * @return Área encontrada o null
     */
    @Transactional(readOnly = true)
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
     * @param evaluationAreaUpdateDTO DTO con datos a actualizar
     * @return ApiResponse<EvaluationArea> con el resultado de la operación
     */
    public ApiResponse<EvaluationArea> update(Integer id, EvaluationAreaUpdateDTO evaluationAreaUpdateDTO) {
        try {
            if (evaluationAreaUpdateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            EvaluationArea evaluationArea = evaluationAreaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + id));

            // Validar que no exista otro área con el mismo nombre
            EvaluationArea existingByName = evaluationAreaRepository.findByName(evaluationAreaUpdateDTO.getName());
            if (existingByName != null && !existingByName.getId().equals(id)) {
                return ApiResponse.error("Ya existe un área de evaluación con el nombre: " + evaluationAreaUpdateDTO.getName());
            }

            // Validar que no exista otro área con el mismo código
            EvaluationArea existingByCode = evaluationAreaRepository.findByCodeArea(evaluationAreaUpdateDTO.getCodeArea());
            if (existingByCode != null && !existingByCode.getId().equals(id)) {
                return ApiResponse.error("Ya existe un área de evaluación con el código: " + evaluationAreaUpdateDTO.getCodeArea());
            }

            evaluationArea.setCodeArea(evaluationAreaUpdateDTO.getCodeArea());
            evaluationArea.setName(evaluationAreaUpdateDTO.getName());

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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse<Void> delete(Integer id) {
        try {
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + id));

            evaluationAreaRepository.delete(evaluationArea);
            return ApiResponse.success("Área de evaluación eliminada exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("No se puede eliminar el Evaluation Area porque tiene Imagenes Medicas asociadas");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar área de evaluación: " + ex.getMessage());
        }
    }
}
