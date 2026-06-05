package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategoryCreateDTO;
import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategoryResponseDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.DatasetCategory.DatasetCategoryMappers;
import Backend.ms_clasificator.Models.Dataset;
import Backend.ms_clasificator.Models.DatasetCategory;
import Backend.ms_clasificator.Repositories.DatasetCategoryRepository;
import Backend.ms_clasificator.Repositories.DatasetRepository;
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
    private DatasetCategoryMappers datasetCategoryMappers;

    @Autowired
    private DatasetRepository datasetRepository;

    /**
     * Obtener todos los estados UI
     * @return Lista de todos los estados
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DatasetCategoryResponseDTO>> findAll() {
        try {
            List<DatasetCategoryResponseDTO> response = datasetCategoryRepository.findAll()
                    .stream()
                    .map(datasetCategoryMappers::toResponseDTO)
                    .toList();
            return ApiResponse.success(response, "Categorias de dataset obtenidas exitosamente");
        } catch (Exception ex) {
            return ApiResponse.error("Error al listar categorias de dataset: " + ex.getMessage());
        }
    }

    /**
     * Obtener un estado UI por ID
     * @param id ID del estado
     * @return UIState encontrado o null
     */
    @Transactional(readOnly = true)
    public ApiResponse<DatasetCategoryResponseDTO> findById(Integer id) {
        try {
            DatasetCategory datasetCategory =  datasetCategoryRepository.findById(id).orElseThrow(() ->
                    new IllegalArgumentException("Estado UI no encontrado con ID: " + id));

            return ApiResponse.success(datasetCategoryMappers.toResponseDTO(datasetCategory), "Estado UI encontrado");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar estado por ID: " + ex.getMessage());
        }
    }

    /**
     * Obtener todos las categorias de un dataset
     * @param datasetId ID del dataset
     * @return Lista de estados de la configuración
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DatasetCategoryResponseDTO>> findByDatasetId(Integer datasetId) {
        try {
            this.datasetRepository.findById(datasetId).orElseThrow(() ->
                    new IllegalArgumentException("Dataset no encontrada con ID: " + datasetId));

            List<DatasetCategory> datasetCategories = this.datasetCategoryRepository.findByDatasetId(datasetId);
            List<DatasetCategoryResponseDTO> response = datasetCategories.stream()
                    .map(datasetCategoryMappers::toResponseDTO)
                    .toList();
            if (datasetCategories.isEmpty()) {
                return ApiResponse.success(response,"No se encontraron estados UI para la configuración con ID: " + datasetId);
            }else{
                return ApiResponse.success(response,"Estados UI encontrados para la configuración con ID: " + datasetId);
            }

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar estado por ID: " + ex.getMessage());
        }
    }

    /**
     * Crear un nuevo estado UI
     * @param datasetCategoryCreateDTO DTO con datos de entrada
     * @return ApiResponse<UIState> con el resultado de la operación
     */
    @Transactional
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

            // Validar el numero de categorias que tiene el dataset para asiganrle el siguiente en secuencia.
            List<DatasetCategory> datasetCategories = datasetCategoryRepository.findByDatasetId(dataset.getId());
            int nextNumValue = datasetCategories.size() + 1;
            datasetCategory.setNumValue(nextNumValue);

            DatasetCategory saved = datasetCategoryRepository.save(datasetCategory);
            return ApiResponse.success(datasetCategoryMappers.toResponseDTO(saved), "Categoria de dataset creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al crear categoria de dataset: " + ex.getMessage());
        }
    }

    /**
     * Eliminar una categoria de un dataset por ID
     * @param id ID del estado a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            DatasetCategory datasetCategory = datasetCategoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria de dataset no encontrada con ID: " + id));

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
