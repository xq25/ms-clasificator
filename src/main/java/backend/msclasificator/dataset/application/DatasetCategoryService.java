package backend.msclasificator.dataset.application;

import backend.msclasificator.dataset.api.dto.DatasetCategoryCreateDTO;
import backend.msclasificator.dataset.api.dto.DatasetCategoryResponseDTO;
import backend.msclasificator.dataset.api.dto.DatasetCategorySummaryDTO;
import backend.msclasificator.dataset.api.dto.DatasetCategoryUpdateDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.dataset.api.mapper.DatasetCategoryMapper;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.dataset.application.port.out.DatasetCategoryRepository;
import backend.msclasificator.dataset.application.port.out.DatasetRepository;
import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DatasetCategoryService {

    @Autowired
    private DatasetCategoryRepository datasetCategoryRepository;

    @Autowired
    private DatasetCategoryMapper datasetCategoryMappers;

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private ImageClassificationRepository imageClassificationRepository;

    /**
     * Obtener todos las categorias (general)
     * @return Lista de todos los estados
     */
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<DatasetCategoryResponseDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<DatasetCategoryResponseDTO> page = datasetCategoryRepository.findAll(pageRequest.toPageQuery())
                .map(datasetCategoryMappers::toResponseDTO);

        return ApiResponse.success(
                PagedResponse.<DatasetCategoryResponseDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Categorias de dataset obtenidas exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(datasetCategoryRepository.count(), "Total de categorías de dataset");
    }

    /**
     * Obtener una categoria por ID
     * @param id ID del estado
     * @return UIState encontrado o null
     */
    @Transactional(readOnly = true)
    public ApiResponse<DatasetCategoryResponseDTO> findById(Integer id) {
        try {
            DatasetCategoryResponseDTO datasetCategory =  datasetCategoryRepository.findById(id)
                    .map(datasetCategoryMappers::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada con ID: " + id));

            return ApiResponse.success(datasetCategory, "Categoria encontrada");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Obtener todos las categorias de un dataset
     * @param datasetId ID del dataset
     * @return Lista de estados de la configuración
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DatasetCategorySummaryDTO>> findByDatasetId(Integer datasetId) {
        try {
            if(!this.datasetRepository.existsById(datasetId)){
                return ApiResponse.error("Dataset no encontrado con ID: " + datasetId);
            }


            List<DatasetCategorySummaryDTO> response = this.datasetCategoryRepository.findByDatasetId(datasetId).stream()
                    .map(datasetCategoryMappers::toSummaryDTO)
                    .toList();
            if (response.isEmpty()) {
                return ApiResponse.success(response,"No se encontraron categorias para la configuración con ID: " + datasetId);
            }else{
                return ApiResponse.success(response,"Categorias encontradas para la configuración con ID: " + datasetId);
            }

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Crear una nueva Categoria
     * @param datasetCategoryCreateDTO DTO con datos de entrada
     * @return ApiResponse<DatasetCategoryResponseDTO> con el resultado de la operación
     */
    public ApiResponse<DatasetCategoryResponseDTO> create(DatasetCategoryCreateDTO datasetCategoryCreateDTO) {
        try {
            if (datasetCategoryCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que exista el dataset al que esta asociado
            Dataset dataset = datasetRepository.findById(datasetCategoryCreateDTO.getDatasetId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Dataset no encontrada con ID: " + datasetCategoryCreateDTO.getDatasetId()));

            DatasetCategory datasetCategory = datasetCategoryMappers.toEntity(datasetCategoryCreateDTO);
            datasetCategory.setDataset(dataset);

            // Validar el numero de categorias que tiene el dataset para asignarle el siguiente en secuencia.
            int nextNumValue = datasetCategoryRepository.findByDatasetId(dataset.getId()).size() + 1;
            datasetCategory.setNumValue(nextNumValue);

            return ApiResponse.success(datasetCategoryMappers.toResponseDTO(datasetCategoryRepository.save(datasetCategory)), "Categoria de dataset creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<DatasetCategoryResponseDTO> update(Integer id, DatasetCategoryUpdateDTO datasetCategoryCreateDTO) {
        try {
            if (datasetCategoryCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            DatasetCategory datasetCategory = datasetCategoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria de dataset no encontrada con ID: " + id));

            // Solo se puede actualizar el nombre de la categoria, por coherencia secuencia de los numValues
            datasetCategory.setName(datasetCategoryCreateDTO.getName());

            return ApiResponse.success(datasetCategoryMappers.toResponseDTO(datasetCategoryRepository.save(datasetCategory)), "Categoria de dataset actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Eliminar una categoria de un dataset por ID
     * @param id ID del estado a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<Void> delete(Integer id) {
        try {
            DatasetCategory datasetCategory = datasetCategoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria de dataset no encontrada con ID: " + id));

            // Las clasificaciones de los doctores referencian la categoría: no se pierden (ADR-010)
            if (imageClassificationRepository.existsByDatasetCategoryId(id)) {
                return ApiResponse.error("No se puede eliminar la categoría: tiene imágenes clasificadas");
            }

            // Solo se puede eliminar la ultima categoria generada de un Dataset, por coherencia secuencia de los numValues
                List<DatasetCategory> datasetCategories = datasetCategoryRepository.findByDatasetId(datasetCategory.getDataset().getId());
                int maxNumValue = datasetCategories.stream().mapToInt(DatasetCategory::getNumValue).max().orElse(0);
                if (datasetCategory.getNumValue() != maxNumValue) {
                    return ApiResponse.error("Solo se puede eliminar la ultima categoria generada de un Dataset, por coherencia secuencia de los numValues");
                }

            datasetCategoryRepository.delete(datasetCategory);
            return ApiResponse.success("Categoria de dataset eliminada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar Categoria de dataset: " + ex.getMessage());
        }
    }
}
