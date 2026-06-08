package Backend.ms_clasificator.Mappers.DatasetCategory;

import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategoryCreateDTO;
import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategoryResponseDTO;
import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategorySummaryDTO;
import Backend.ms_clasificator.Mappers.Dataset.DatasetMappers;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.DatasetCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatasetCategoryMappers implements Mapper<DatasetCategory, DatasetCategoryCreateDTO, DatasetCategoryResponseDTO, DatasetCategorySummaryDTO> {

    @Autowired
    private DatasetMappers datasetMapper;

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
    public DatasetCategoryResponseDTO toResponseDTO(DatasetCategory datasetCategory) {
        if (datasetCategory == null) {
            return null;
        }

        return DatasetCategoryResponseDTO.builder()
                .id(datasetCategory.getId())
                .numValue(datasetCategory.getNumValue())
                .dataset(datasetCategory.getDataset() != null ? datasetMapper.toSummaryDTO(datasetCategory.getDataset()) : null)
                .build();
    }

    @Override
    public DatasetCategorySummaryDTO toSummaryDTO(DatasetCategory datasetCategory) {
        if (datasetCategory == null) {
            return null;
        }

        return DatasetCategorySummaryDTO.builder()
                .id(datasetCategory.getId())
                .numValue(datasetCategory.getNumValue())
                .build();
    }
}