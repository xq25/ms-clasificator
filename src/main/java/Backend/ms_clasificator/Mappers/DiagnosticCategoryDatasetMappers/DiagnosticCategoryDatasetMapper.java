package Backend.ms_clasificator.Mappers.DiagnosticCategoryDatasetMappers;

import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetCreateDTO;
import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetResponseDTO;
import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetSummaryDTO;
import Backend.ms_clasificator.Mappers.DatasetCategory.DatasetCategoryMappers;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Mappers.MedicalDiagnosticMappers.MedicalDiagnosticMapper;
import Backend.ms_clasificator.Models.DiagnosticCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiagnosticCategoryDatasetMapper implements Mapper<DiagnosticCategoryDataset, DiagnosticCategoryDatasetCreateDTO, DiagnosticCategoryDatasetResponseDTO, DiagnosticCategoryDatasetSummaryDTO> {

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Autowired
    private DatasetCategoryMappers datasetCategoryMapper;

    @Override
    public DiagnosticCategoryDataset toEntity(DiagnosticCategoryDatasetCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return DiagnosticCategoryDataset.builder()
                .build();
    }

    @Override
    public DiagnosticCategoryDatasetCreateDTO toDTO(DiagnosticCategoryDataset entity) {
        if (entity == null) {
            return null;
        }

        return DiagnosticCategoryDatasetCreateDTO.builder()
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
    public DiagnosticCategoryDatasetResponseDTO toResponseDTO(DiagnosticCategoryDataset entity) {
        if (entity == null) {
            return null;
        }

        return DiagnosticCategoryDatasetResponseDTO.builder()
                .id(entity.getId())
                .datasetCategory(entity.getDatasetCategory() != null ? datasetCategoryMapper.toSummaryDTO(entity.getDatasetCategory()) : null)
                .medicalDiagnostic(entity.getMedicalDiagnostic() != null? medicalDiagnosticMapper.toSummaryDTO(entity.getMedicalDiagnostic()) : null)
                .build();
    }

    @Override
    public DiagnosticCategoryDatasetSummaryDTO toSummaryDTO(DiagnosticCategoryDataset entity) {
        if (entity == null) {
            return null;
        }

        return DiagnosticCategoryDatasetSummaryDTO.builder()
                .id(entity.getId())
                .medicalDiagnostic(entity.getMedicalDiagnostic() != null ? medicalDiagnosticMapper.toSummaryDTO(entity.getMedicalDiagnostic()): null)
                .build();
    }
}