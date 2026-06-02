package Backend.ms_clasificator.Mappers.DatasetCategory;

import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategoryCreateDTO;
import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategoryResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.DatasetCategory;
import org.springframework.stereotype.Component;

@Component
public class DatasetCategoryMappers implements Mapper<DatasetCategory, DatasetCategoryCreateDTO, DatasetCategoryResponseDTO> {

    @Override
    public DatasetCategory toEntity(DatasetCategoryCreateDTO datasetCategoryCreateDTO) {
        if (datasetCategoryCreateDTO == null) {
            return null;
        }

        return DatasetCategory.builder()
                // Las relaciones se asignan en el Service
                .build();
    }

    @Override
    public DatasetCategoryCreateDTO toDTO(DatasetCategory datasetCategory) {
        if (datasetCategory == null) {
            return null;
        }

        return DatasetCategoryCreateDTO.builder()
                .datasetId(datasetCategory.getDataset() != null ? datasetCategory.getDataset().getId() : null)
                .build();
    }

    @Override
    public DatasetCategoryResponseDTO toResponseDTO(DatasetCategory datasetCategory){
        if (datasetCategory == null){
            return null;
        }
        return DatasetCategoryResponseDTO.builder()
                .id(datasetCategory.getId())
                .numValue(datasetCategory.getNumValue())
                .build();
    }
}
