package backend.msclasificator.dataset.application;

import backend.msclasificator.dataset.api.dto.DatasetCreateDTO;
import backend.msclasificator.dataset.api.dto.DatasetResponseDTO;
import backend.msclasificator.dataset.api.dto.DatasetUpdateDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.dataset.api.mapper.DatasetMapper;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.medicalimage.domain.MedicalImageType;
import backend.msclasificator.dataset.application.port.out.DatasetCategoryRepository;
import backend.msclasificator.dataset.application.port.out.DatasetRepository;
import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.diagnosiscatalog.application.port.out.MedicalDiagnosticRepository;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DatasetService {

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private DatasetMapper datasetMappers;

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Autowired
    private MedicalImageTypeRepository medicalImageTypeRepository;

    @Autowired
    private DatasetCategoryRepository datasetCategoryRepository;

    @Autowired
    private ImageClassificationRepository imageClassificationRepository;

    /**
     * Obtener todas las configuraciones UI
     * @return Lista de todas las configuraciones
     */
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<DatasetResponseDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<DatasetResponseDTO> page = datasetRepository.findAll(pageRequest.toPageQuery())
                .map(datasetMappers::toResponseDTO);

        return ApiResponse.success(
                PagedResponse.<DatasetResponseDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Datasets obtenidos exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(datasetRepository.count(), "Total de Datasets");
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
    @Transactional
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

            if (datasetRepository.existsByName(datasetCreateDTO.getName())) {
                return ApiResponse.error("Ya existe un dataset con el nombre: " + datasetCreateDTO.getName());
            }

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

            // Las validaciones de unicidad excluyen al propio dataset
            if (datasetRepository.existsByNameAndIdNot(datasetUpdateDTO.getName(), id)) {
                return ApiResponse.error("Ya existe un dataset con el nombre: " + datasetUpdateDTO.getName());
            }

            if (datasetRepository.existsByMedicalDiagnosticIdAndIdNot(datasetUpdateDTO.getMedicalDiagnosticId(), id)) {
                return ApiResponse.error("Ya existe un Dataset para clasificar este diagnóstico médico");
            }

            if (datasetRepository.existsByMedicalImageTypeIdAndIdNot(datasetUpdateDTO.getMedicalImageTypeId(), id)) {
                return ApiResponse.error("Ya existe un Dataset para clasificar este tipo de imagen médica");
            }

            // Las categorías solo pueden contener sub-diagnósticos del diagnóstico del dataset,
            // por eso el diagnóstico no puede cambiar si ya hay categorías definidas.
            boolean cambiaDiagnostico = !dataset.getMedicalDiagnostic().getId().equals(datasetUpdateDTO.getMedicalDiagnosticId());
            if (cambiaDiagnostico) {
                List<DatasetCategory> datasetCategories = this.datasetCategoryRepository.findByDatasetId(id);
                if (!datasetCategories.isEmpty()) {
                    return ApiResponse.error("No se puede cambiar el diagnóstico médico de un Dataset que ya tiene categorías definidas para clasificación. Por favor, elimine primero las categorías asociadas a este Dataset antes");
                }
            }

            dataset.setName(datasetUpdateDTO.getName());
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

            // Las clasificaciones de los doctores referencian sus categorías: no se pierden (ADR-010)
            if (imageClassificationRepository.existsByDatasetCategoryDatasetId(id)) {
                return ApiResponse.error("No se puede eliminar el dataset: tiene imágenes clasificadas en sus categorías");
            }

            datasetRepository.delete(dataset);
            return ApiResponse.success("Dataset eliminada exitosamente (y sus categories asociados)");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar Dataset: " + ex.getMessage());
        }
    }
}
