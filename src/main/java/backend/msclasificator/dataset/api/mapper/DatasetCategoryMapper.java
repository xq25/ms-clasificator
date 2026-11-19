package backend.msclasificator.dataset.api.mapper;

import backend.msclasificator.dataset.api.dto.DatasetCategoryCreateDTO;
import backend.msclasificator.dataset.api.dto.DatasetCategoryResponseDTO;
import backend.msclasificator.dataset.api.dto.DatasetCategorySummaryDTO;
import backend.msclasificator.dataset.api.mapper.DatasetMapper;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.dataset.domain.DatasetCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatasetCategoryMapper implements Mapper<DatasetCategory, DatasetCategoryCreateDTO, DatasetCategoryResponseDTO, DatasetCategorySummaryDTO> {

    @Autowired
    private DatasetMapper datasetMapper;

    @Override
    public DatasetCategory toEntity(DatasetCategoryCreateDTO datasetCategoryCreateDTO) {
        if (datasetCategoryCreateDTO == null) {
            return null;
        }

        return DatasetCategory.builder()
                .name(datasetCategoryCreateDTO.getName())
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
                .name(datasetCategory.getName())
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
                .name(datasetCategory.getName())
                .numValue(datasetCategory.getNumValue())
                .build();
    }
}