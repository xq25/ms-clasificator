package backend.msclasificator.dataset.api.mapper;

import backend.msclasificator.dataset.api.dto.CategoryDiagnosticCreateDTO;
import backend.msclasificator.dataset.api.dto.CategoryDiagnosticResponseDTO;
import backend.msclasificator.dataset.api.dto.CategoryDiagnosticSummaryDTO;
import backend.msclasificator.dataset.api.mapper.DatasetCategoryMapper;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.diagnosiscatalog.api.mapper.MedicalDiagnosticMapper;
import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryDiagnosticMapper implements Mapper<CategoryDiagnostic, CategoryDiagnosticCreateDTO, CategoryDiagnosticResponseDTO, CategoryDiagnosticSummaryDTO> {

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Autowired
    private DatasetCategoryMapper datasetCategoryMapper;

    @Override
    public CategoryDiagnostic toEntity(CategoryDiagnosticCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return CategoryDiagnostic.builder()
                .build();
    }

    @Override
    public CategoryDiagnosticCreateDTO toDTO(CategoryDiagnostic entity) {
        if (entity == null) {
            return null;
        }

        return CategoryDiagnosticCreateDTO.builder()
                .datasetCategoryId(
                        entity.getDatasetCategory() != null
                                ? entity.getDatasetCategory().getId()
                                : null
                )
                .medicalDiagnosticId(
                        entity.getMedicalDiagnostic() != null
                                ? entity.getMedicalDiagnostic().getId()
                                : null
                )
                .build();
    }

    @Override
    public CategoryDiagnosticResponseDTO toResponseDTO(CategoryDiagnostic entity) {
        if (entity == null) {
            return null;
        }

        return CategoryDiagnosticResponseDTO.builder()
                .id(entity.getId())
                .datasetCategory(entity.getDatasetCategory() != null ? datasetCategoryMapper.toSummaryDTO(entity.getDatasetCategory()) : null)
                .medicalDiagnostic(entity.getMedicalDiagnostic() != null? medicalDiagnosticMapper.toSummaryDTO(entity.getMedicalDiagnostic()) : null)
                .build();
    }

    @Override
    public CategoryDiagnosticSummaryDTO toSummaryDTO(CategoryDiagnostic entity) {
        if (entity == null) {
            return null;
        }

        return CategoryDiagnosticSummaryDTO.builder()
                .id(entity.getId())
                .medicalDiagnostic(entity.getMedicalDiagnostic() != null ? medicalDiagnosticMapper.toSummaryDTO(entity.getMedicalDiagnostic()): null)
                .build();
    }
}