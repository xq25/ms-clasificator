package Backend.ms_clasificator.Mappers.DiagnosticCategoryDatasetMappers;

import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetCreateDTO;
import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.DiagnosticCategoryDataset;
import org.springframework.stereotype.Component;

@Component
public class DiagnosticCategoryDatasetMapper implements Mapper<DiagnosticCategoryDataset, DiagnosticCategoryDatasetCreateDTO, DiagnosticCategoryDatasetResponseDTO> {

    @Override
    public DiagnosticCategoryDataset toEntity(
            DiagnosticCategoryDatasetCreateDTO dto) {

        if (dto == null) {
            return null;
        }

        return DiagnosticCategoryDataset.builder()
                .build();
    }

    @Override
    public DiagnosticCategoryDatasetCreateDTO toDTO(
            DiagnosticCategoryDataset entity) {

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
    public DiagnosticCategoryDatasetResponseDTO toResponseDTO(
            DiagnosticCategoryDataset entity) {

        if (entity == null) {
            return null;
        }

        return DiagnosticCategoryDatasetResponseDTO.builder()
                .id(entity.getId())
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
                .medicalDiagnosticCode(
                        entity.getMedicalDiagnostic() != null
                                ? entity.getMedicalDiagnostic().getDiagnosticCode()
                                : null
                )
                .build();
    }
}
