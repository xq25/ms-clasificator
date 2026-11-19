package backend.msclasificator.dataset.api.mapper;

import backend.msclasificator.dataset.api.dto.DatasetCreateDTO;
import backend.msclasificator.dataset.api.dto.DatasetResponseDTO;
import backend.msclasificator.dataset.api.dto.DatasetSummaryDTO;
import backend.msclasificator.dataset.api.dto.DatasetUpdateDTO;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.diagnosiscatalog.api.mapper.MedicalDiagnosticMapper;
import backend.msclasificator.medicalimage.api.mapper.MedicalImageTypeMapper;
import backend.msclasificator.dataset.domain.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatasetMapper implements Mapper<Dataset, DatasetCreateDTO, DatasetResponseDTO, DatasetSummaryDTO> {

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