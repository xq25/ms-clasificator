package backend.msclasificator.doctor.application;

import backend.msclasificator.doctor.api.dto.EvaluationAreaCreateDTO;
import backend.msclasificator.doctor.api.dto.EvaluationAreaResponseDTO;
import backend.msclasificator.doctor.api.dto.EvaluationAreaSummaryDTO;
import backend.msclasificator.doctor.api.dto.EvaluationAreaUpdateDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.doctor.api.mapper.EvaluationAreaMapper;
import backend.msclasificator.doctor.domain.EvaluationArea;
import backend.msclasificator.doctor.application.port.out.EvaluationAreaRepository;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EvaluationAreaService {

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;

    @Autowired
    private EvaluationAreaMapper evaluationAreaMapper;

    @Autowired
    private MedicalImageTypeRepository medicalImageTypeRepository;

    /**
     * Obtener todas las áreas de evaluación
     * @return Lista de todas las áreas de evaluación
     */
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<EvaluationAreaResponseDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<EvaluationAreaResponseDTO> page = evaluationAreaRepository.findAll(pageRequest.toPageQuery())
                .map(evaluationAreaMapper::toResponseDTO);

        return ApiResponse.success(
                PagedResponse.<EvaluationAreaResponseDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Areas de evaluacion obtenidas exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<EvaluationAreaSummaryDTO>> searchByName(String query, PageRequestDTO pageRequest) {
        try {
            PageResult<EvaluationAreaSummaryDTO> page = evaluationAreaRepository
                    .searchByName(query, pageRequest.toPageQuery())
                    .map(evaluationAreaMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<EvaluationAreaSummaryDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
                            .build(),
                    "Resultados de búsqueda por nombre"
            );
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar áreas de evaluación por nombre: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<EvaluationAreaSummaryDTO>> searchByCode(String query, PageRequestDTO pageRequest) {
        try {
            PageResult<EvaluationAreaSummaryDTO> page = evaluationAreaRepository
                    .searchByCode(query, pageRequest.toPageQuery())
                    .map(evaluationAreaMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<EvaluationAreaSummaryDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
                            .build(),
                    "Resultados de búsqueda por código"
            );
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar áreas de evaluación por código: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(evaluationAreaRepository.count(), "Total de áreas de evaluación");
    }

    /**
     * Obtener un área de evaluación por ID
     * @param id ID del área de evaluación
     * @return Área encontrada o null
     */
    @Transactional(readOnly = true)
    public ApiResponse<EvaluationAreaResponseDTO> findById(Integer id) {
        try{
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(id).
                    orElseThrow(() -> new IllegalArgumentException(
                            "Area de Evaluacion no encontrado con ID: " + id));

            return ApiResponse.success(evaluationAreaMapper.toResponseDTO(evaluationArea), "Area de Evaluacion encontrada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Crear una nueva área de evaluación
     * @param evaluationAreaCreateDTO DTO con datos de entrada
     * @return ApiResponse<EvaluationArea> con el resultado de la operación
     */
    public ApiResponse<EvaluationAreaResponseDTO> create(EvaluationAreaCreateDTO evaluationAreaCreateDTO) {

            if (evaluationAreaCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que no exista área con el mismo nombre
            if (this.evaluationAreaRepository.existsByName(evaluationAreaCreateDTO.getName())) {
                return ApiResponse.error("Ya existe un área de evaluación con el nombre: " + evaluationAreaCreateDTO.getName());
            }

            // Validar que no exista área con el mismo código
            if (this.evaluationAreaRepository.existsByCodeArea(evaluationAreaCreateDTO.getCodeArea())) {
                return ApiResponse.error("Ya existe un área de evaluación con el código: " + evaluationAreaCreateDTO.getCodeArea());
            }

            EvaluationArea evaluationArea = evaluationAreaMapper.toEntity(evaluationAreaCreateDTO);
            evaluationArea.setDoctorsCount(0); // Inicializamos el contador de doctores en 0
            return ApiResponse.success(evaluationAreaMapper.toResponseDTO(evaluationAreaRepository.save(evaluationArea)), "Área de evaluación creada exitosamente");
    }

    /**
     * Actualizar un área de evaluación existente
     * @param id ID del área a actualizar
     * @param evaluationAreaUpdateDTO DTO con datos a actualizar
     * @return ApiResponse<EvaluationArea> con el resultado de la operación
     */
    @Transactional
    public ApiResponse<EvaluationAreaResponseDTO> update(Integer id, EvaluationAreaUpdateDTO evaluationAreaUpdateDTO) {
        try {
            if (evaluationAreaUpdateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            EvaluationArea evaluationArea = evaluationAreaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + id));

            // Validar que no exista otro área con el mismo nombre
            EvaluationArea existingByName = evaluationAreaRepository.findByName(evaluationAreaUpdateDTO.getName()).orElse(null);
            if (existingByName != null && !existingByName.getId().equals(id)) {
                return ApiResponse.error("Ya existe un área de evaluación con el nombre: " + evaluationAreaUpdateDTO.getName());
            }

            // Validar que no exista otro área con el mismo código
            EvaluationArea existingByCode = evaluationAreaRepository.findByCodeArea(evaluationAreaUpdateDTO.getCodeArea()).orElse(null);
            if (existingByCode != null && !existingByCode.getId().equals(id)) {
                return ApiResponse.error("Ya existe un área de evaluación con el código: " + evaluationAreaUpdateDTO.getCodeArea());
            }

            evaluationArea.setCodeArea(evaluationAreaUpdateDTO.getCodeArea());
            evaluationArea.setName(evaluationAreaUpdateDTO.getName());

            EvaluationArea updated = evaluationAreaRepository.save(evaluationArea);
            return ApiResponse.success(evaluationAreaMapper.toResponseDTO(updated), "Área de evaluación actualizada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Eliminar un área de evaluación por ID
     * @param id ID del área a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + id));

            // Validamos que no tenga tipos de imagenes medicas asociadas a esta area
            if (medicalImageTypeRepository.existsByEvaluationAreaId(id)) {
                return ApiResponse.error("No se puede eliminar el área de evaluación porque tiene tipos de imágenes médicas asociadas");
            }

            evaluationAreaRepository.delete(evaluationArea);
            return ApiResponse.success("Área de evaluación eliminada exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violacion de integridad de la base de datos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar área de evaluación: " + ex.getMessage());
        }
    }
}
