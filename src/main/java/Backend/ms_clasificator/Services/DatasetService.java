package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Dataset.DatasetCreateDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetResponseDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetSummaryDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.Dataset.DatasetMappers;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Models.Dataset;
import Backend.ms_clasificator.Models.DatasetCategory;
import Backend.ms_clasificator.Models.MedicalImageType;
import Backend.ms_clasificator.Repositories.*;
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
    private MedicalImageTypeRepository medicalImageTypeRepository;

    @Autowired
    private DatasetCategoryRepository datasetCategoryRepository;

    /**
     * Obtener todas las configuraciones UI
     * @return Lista de todas las configuraciones
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DatasetSummaryDTO>> findAll() {

        List<DatasetSummaryDTO> datasets = datasetRepository.findAll()
                .stream()
                .map(datasetMappers::toSummaryDTO)
                .toList();
        return ApiResponse.success(datasets, "Datasets obtenidos exitosamente");

    }

    /**
     * Obtener una configuración UI por ID
     * @param id ID de la configuración
     * @return UIConfig encontrada o null
     */
    @Transactional(readOnly = true)
    public ApiResponse<DatasetResponseDTO> findById(Integer id) {
        try {
            DatasetResponseDTO dataset = datasetRepository.findById(id)
                    .map(datasetMappers::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Dataset no encontrado con ID: " + id));

            return ApiResponse.success(dataset, "Dataset encontrado");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<DatasetResponseDTO> findByMedicalImageTypeId(Integer medicalImageTypeId) {
        try {
            // Validamos que el area de evaluacion exista
            if (!medicalImageTypeRepository.existsById(medicalImageTypeId)) {
                return ApiResponse.error("Tipo de imagen medica no encontrada con ID: " + medicalImageTypeId);
            }

            DatasetResponseDTO dataset = this.datasetRepository.findByMedicalImageTypeId(medicalImageTypeId).map(datasetMappers::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró un Dataset asignado a clasificar el tipo de imagen medica con ID: " + medicalImageTypeId));

            return ApiResponse.success(dataset, "Dataset encontrado para el tipo de imagen medica con ID: " + medicalImageTypeId);

        }catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }

    }

    /**
     * Obtener todas las dataset de un diagnóstico médico
     * @param medicalDiagnosticId ID del diagnóstico médico
     * @return Lista de Datasets para clasificaciones del diagnostico
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DatasetResponseDTO>> findByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        try{
            if(!medicalDiagnosticRepository.existsById(medicalDiagnosticId)){
                return ApiResponse.error("Diagnóstico médico no encontrado con ID: " + medicalDiagnosticId);
            }

            List<DatasetResponseDTO> datasets = datasetRepository.findByMedicalDiagnosticId(medicalDiagnosticId)
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
     * Crear una nueva dataset
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

            // Validar que exista ese tipo de imagen medica
            MedicalImageType medicalImageType = medicalImageTypeRepository
                    .findById(datasetCreateDTO.getMedicalImageTypeId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Tipo de imagen médica no encontrado con ID: " + datasetCreateDTO.getMedicalImageTypeId()));

            // Validar que no exista ya un dataset para este diagnóstico
            if (datasetRepository.existsByMedicalDiagnosticId(datasetCreateDTO.getMedicalDiagnosticId())) {
                return ApiResponse.error("Ya existe una dataset para este diagnóstico médico");
            }

            //Validar que no exista ya un dataset para ese tipo de imagen
            if(datasetRepository.existsByMedicalImageTypeId(datasetCreateDTO.getMedicalImageTypeId())){
                return ApiResponse.error("Ya existe una dataset para clasificar este tipo de imagen médica");
            }

            Dataset dataset = datasetMappers.toEntity(datasetCreateDTO);
            dataset.setMedicalDiagnostic(medicalDiagnostic);
            dataset.setMedicalImageType(medicalImageType);

            return ApiResponse.success(datasetMappers.toResponseDTO(datasetRepository.save(dataset)), "Dataset creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Actualizar un Dataset existente - changeMedicalDiagnostic
     * @param id ID de la configuración a actualizar
     * @param datasetUpdateDTO DTO con datos a actualizar
     * @return ApiResponse<UIConfig> con el resultado de la operación
     */
    @Transactional
    public ApiResponse<DatasetResponseDTO> update(Integer id, DatasetUpdateDTO datasetUpdateDTO) {
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

            // Validar que exista el nuevo tipo de imagen a clasificar
            MedicalImageType medicalImageType = medicalImageTypeRepository
                    .findById(datasetUpdateDTO.getMedicalImageTypeId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Tipo de imagen médica no encontrado con ID: " + datasetUpdateDTO.getMedicalImageTypeId()));

            // Validamos que no exista ya un dataset para ese diagnostico
            if (datasetRepository.existsByMedicalDiagnosticId(datasetUpdateDTO.getMedicalDiagnosticId())) {
                return ApiResponse.error("Ya existe un Dataset para clasificar este diagnóstico médico");
            }

            // Validamos que no exista ya un dataset para clasificar este tipo de imagen
            if(datasetRepository.existsByMedicalImageTypeId(datasetUpdateDTO.getMedicalImageTypeId())) {
                return ApiResponse.error("Ya existe un Dataset para clasificar este tipo de imagen médica");
            }

            // Validar que el Dataset no tenga ya categorias definidas para clasificacion.
            // Al cambiar el diagnostico, todos las categorias quedan inutiles,
            // ya que deben tener unicamente subDiagnosticos de este diagnostico padre.
            List<DatasetCategory> datasetCategories = this.datasetCategoryRepository.findByDatasetId(id);
            if (!datasetCategories.isEmpty()){
                return ApiResponse.error("No se puede cambiar el diagnóstico médico de un Dataset que ya tiene categorías definidas para clasificación. Por favor, elimine primero las categorías asociadas a este Dataset antes");
            }

            dataset.setMedicalDiagnostic(medicalDiagnostic);
            dataset.setMedicalImageType(medicalImageType);
            return ApiResponse.success(datasetMappers.toResponseDTO(datasetRepository.save(dataset)), "Dataset actualizado exitosamente");

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
}
