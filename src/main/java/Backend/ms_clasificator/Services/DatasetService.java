package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Dataset.DatasetCreateDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetResponseDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.Dataset.DatasetMappers;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Models.Dataset;
import Backend.ms_clasificator.Models.DatasetCategory;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import Backend.ms_clasificator.Repositories.MedicalDiagnosticRepository;
import Backend.ms_clasificator.Repositories.DatasetRepository;
import Backend.ms_clasificator.Repositories.DatasetCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DatasetService {

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private DatasetMappers datasetMappers;

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;

    @Autowired
    private DatasetCategoryRepository datasetCategoryRepository;

    /**
     * Obtener todas las configuraciones UI
     * @return Lista de todas las configuraciones
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DatasetResponseDTO>> findAll() {
        try {
            List<DatasetResponseDTO> datasets = datasetRepository.findAll()
                    .stream()
                    .map(datasetMappers::toResponseDTO)
                    .toList();
            return ApiResponse.success(datasets, "Datasets obtenidos exitosamente");
        } catch (Exception ex) {
            return ApiResponse.error("Error al listar Datasets: " + ex.getMessage());
        }
    }

    /**
     * Obtener una configuración UI por ID
     * @param id ID de la configuración
     * @return UIConfig encontrada o null
     */
    @Transactional(readOnly = true)
    public ApiResponse<DatasetResponseDTO> findById(Integer id) {
        try {
            Dataset dataset = datasetRepository.findById(id).orElseThrow(() ->
                    new IllegalArgumentException("Dataset no encontrado con ID: " + id));
            return ApiResponse.success(datasetMappers.toResponseDTO(dataset), "Dataset encontrado");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar Dataset por ID: " + ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<DatasetResponseDTO> findByEvaluationAreaId(Integer evaluationAreaId){
        // Validamos que el area de evaluacion exista
        EvaluationArea evaluationArea = this.evaluationAreaRepository.findById(evaluationAreaId).orElse(null);
        if (evaluationArea == null){
            return ApiResponse.error("Area de Evaluacion no encontrada con ID: " + evaluationAreaId);
        }
        Dataset dataset = this.datasetRepository.findByEvaluationAreaId(evaluationAreaId);
        if(dataset == null){
            return ApiResponse.error("No se encontró un Dataset asociado al área de evaluación con ID: " + evaluationAreaId);
        }
        return ApiResponse.success(datasetMappers.toResponseDTO(dataset), "Dataset encontrado para el área de evaluación con ID: " + evaluationAreaId);
    }

    /**
     * Obtener todas las configuraciones UI de un diagnóstico médico
     * @param medicalDiagnosticId ID del diagnóstico médico
     * @return Lista de Datasets para clasificaciones del diagnostico
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DatasetResponseDTO>> findByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        try{
            MedicalDiagnostic medicalDiagnostic = this.medicalDiagnosticRepository.findById(medicalDiagnosticId).orElseThrow(() ->
                    new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + medicalDiagnosticId));

            List<DatasetResponseDTO> datasets = datasetRepository.findByMedicalDiagnostic_Id(medicalDiagnosticId)
                    .stream()
                    .map(datasetMappers::toResponseDTO)
                    .toList();

            if (datasets.isEmpty()) {
                return ApiResponse.success(datasets,"No se encontraron Datasets para clasificar el diagnóstico médico con ID: " + medicalDiagnosticId);
            }else{
                return ApiResponse.success(datasets,"Datasets encontrados para el diagnóstico médico con ID: " + medicalDiagnosticId);
            }

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar configuraciones por ID de diagnóstico médico: " + ex.getMessage());
        }
    }

    /**
     * Crear una nueva configuración UI
     * @param datasetCreateDTO DTO con datos de entrada
     * @return ApiResponse<UIConfig> con el resultado de la operación
     */
    public ApiResponse<DatasetResponseDTO> create(DatasetCreateDTO datasetCreateDTO) {
        try {
            if (datasetCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que exista el diagnóstico médico
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository
                    .findById(datasetCreateDTO.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Diagnóstico médico no encontrado con ID: " + datasetCreateDTO.getMedicalDiagnosticId()));

            // Validar que no exista ya una configuración para este diagnóstico
            if (datasetRepository.existsByMedicalDiagnostic_Id(datasetCreateDTO.getMedicalDiagnosticId())) {
                return ApiResponse.error("Ya existe una configuración UI para este diagnóstico médico");
            }

            Dataset dataset = datasetMappers.toEntity(datasetCreateDTO);
            dataset.setMedicalDiagnostic(medicalDiagnostic);

            Dataset saved = datasetRepository.save(dataset);
            return ApiResponse.success(datasetMappers.toResponseDTO(saved), "Configuración UI creada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al crear configuración UI: " + ex.getMessage());
        }
    }

    /**
     * Actualizar un Dataset existente - changeMedicalDiagnostic
     * @param id ID de la configuración a actualizar
     * @param datasetUpdateDTO DTO con datos a actualizar
     * @return ApiResponse<UIConfig> con el resultado de la operación
     */
    @Transactional
    public ApiResponse<DatasetResponseDTO> changeMedicalDiagnostic(Integer id, DatasetUpdateDTO datasetUpdateDTO) {
        try {
            if (datasetUpdateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            Dataset dataset = datasetRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Dataset no encontrado con ID: " + id));

            // Validar que exista el nuevo diagnóstico médico
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository
                    .findById(datasetUpdateDTO.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Diagnóstico médico no encontrado con ID: " + datasetUpdateDTO.getMedicalDiagnosticId()));

            // Validamos que no exista ya un dataset para ese diagnostico
            if (datasetRepository.existsByMedicalDiagnostic_Id(datasetUpdateDTO.getMedicalDiagnosticId())) {
                return ApiResponse.error("Ya existe un Dataset para clasificar este diagnóstico médico");
            }

            // Validar que el Dataset no tenga ya categorias definidas para clasificacion.
            // Al cambiar el diagnostico, todos las categorias quedan inutiles,
            // ya que deben tener unicamente subDiagnosticos de este diagnostico padre.
            List<DatasetCategory> datasetCategories = this.datasetCategoryRepository.findByUiConfig_Id(id);
            if (!datasetCategories.isEmpty()){
                return ApiResponse.error("No se puede cambiar el diagnóstico médico de un Dataset que ya tiene categorías definidas para clasificación. Por favor, elimine primero las categorías asociadas a este Dataset antes");
            }

            dataset.setMedicalDiagnostic(medicalDiagnostic);

            Dataset updated = datasetRepository.save(dataset);
            return ApiResponse.success(datasetMappers.toResponseDTO(updated), "Configuración UI actualizada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar configuración UI: " + ex.getMessage());
        }
    }

    /**
     * Eliminar una configuración UI por ID
     * Nota: Los UIState asociados se eliminarán automáticamente (cascade)
     * @param id ID de la configuración a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<Void> delete(Integer id) {
        try {
            Dataset dataset = datasetRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Dataset no encontrada con ID: " + id));

            datasetRepository.delete(dataset);
            return ApiResponse.success("Dataset eliminada exitosamente (y sus categories asociados)");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar Dataset: " + ex.getMessage());
        }
    }

    public ApiResponse<DatasetResponseDTO> assingToEvaluationArea(Integer datasetId, Integer evaluationAreaId) {
        try {
            Dataset dataset = datasetRepository.findById(datasetId)
                    .orElseThrow(() -> new IllegalArgumentException("Configuración UI no encontrada con ID: " + datasetId));

            // Validamos que el area al que se va asignar dicha configuracion existe
            EvaluationArea evaluationArea = this.evaluationAreaRepository.findById(evaluationAreaId).orElse(null);
            if (evaluationArea == null){
                return ApiResponse.error("Area de Evaluacion no encontrada con ID: " + evaluationAreaId);
            }

            dataset.setEvaluationArea(evaluationArea);
            Dataset updated = datasetRepository.save(dataset);

            return ApiResponse.success(datasetMappers.toResponseDTO(updated), "Configuración UI asignada exitosamente al área de evaluación");


        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al asignar configuración UI al área de evaluación: " + ex.getMessage());
        }
    }

    public ApiResponse<DatasetResponseDTO> removeFromEvaluationArea(Integer datasetId) {
        try {
            Dataset dataset = datasetRepository.findById(datasetId)
                    .orElseThrow(() -> new IllegalArgumentException("Dataset no encontrado con ID: " + datasetId));

            // Validamos que si tenga Area de evaluacion asignada
            if (dataset.getEvaluationArea() == null){
                return ApiResponse.error("El Dataset con ID: " + datasetId + " no tiene un área de evaluación asignada que remover");
            }

            dataset.setEvaluationArea(null);
            Dataset updated = datasetRepository.save(dataset);

            return ApiResponse.success(datasetMappers.toResponseDTO(updated), "Dataset desvinculado exitosamente del área de evaluación");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al desvincular Dataset del área de evaluación: " + ex.getMessage());
        }
    }

}
