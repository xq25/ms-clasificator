package Backend.ms_clasificator.Mappers.Dataset;

import Backend.ms_clasificator.DTOs.Dataset.DatasetCreateDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetResponseDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetSummaryDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetUpdateDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Mappers.MedicalDiagnosticMappers.MedicalDiagnosticMapper;
import Backend.ms_clasificator.Mappers.MedicalImageTypeMappers.MedicalImageTypeMapper;
import Backend.ms_clasificator.Models.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatasetMappers implements Mapper<Dataset, DatasetCreateDTO, DatasetResponseDTO, DatasetSummaryDTO> {

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Autowired
    private MedicalImageTypeMapper medicalImageTypeMapper;

    @Override
    public Dataset toEntity(DatasetCreateDTO datasetCreateDTO) {
        if (datasetCreateDTO == null) {
            return null;
        }

        return Dataset.builder()
                .name(datasetCreateDTO.getName())
                // Las relaciones se asignan en el Service
                .build();
    }

    public Dataset toEntity(DatasetUpdateDTO datasetUpdateDTO) {
        if (datasetUpdateDTO == null) {
            return null;
        }

        return Dataset.builder()
                .name(datasetUpdateDTO.getName())
                // Las relaciones se asignan en el Service
                .build();
    }

    @Override
    public DatasetCreateDTO toDTO(Dataset dataset) {
        if (dataset == null) {
            return null;
        }

        return DatasetCreateDTO.builder()
                .name(dataset.getName())
                .medicalDiagnosticId(dataset.getMedicalDiagnostic() != null ? dataset.getMedicalDiagnostic().getId() : null)
                .build();
    }

    @Override
    public DatasetResponseDTO toResponseDTO(Dataset dataset) {
        if (dataset == null) {
            return null;
        }

        return DatasetResponseDTO.builder()
                .id(dataset.getId())
                .name(dataset.getName())
                .medicalDiagnostic(dataset.getMedicalDiagnostic() != null ? medicalDiagnosticMapper.toSummaryDTO(dataset.getMedicalDiagnostic()) : null)
                .medicalImageType(dataset.getMedicalImageType() != null ? medicalImageTypeMapper.toSummaryDTO(dataset.getMedicalImageType()) : null)
                .build();
    }

    @Override
    public DatasetSummaryDTO toSummaryDTO(Dataset dataset) {
        if (dataset == null) {
            return null;
        }

        return DatasetSummaryDTO.builder()
                .id(dataset.getId())
                .name(dataset.getName())
                .medicalDiagnostic(dataset.getMedicalDiagnostic() != null ? medicalDiagnosticMapper.toSummaryDTO(dataset.getMedicalDiagnostic()) : null)
                .build();
    }
}