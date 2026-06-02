package Backend.ms_clasificator.Mappers.Dataset;

import Backend.ms_clasificator.DTOs.Dataset.DatasetCreateDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Mappers.MedicalDiagnosticMappers.MedicalDiagnosticMapper;
import Backend.ms_clasificator.Models.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatasetMappers implements Mapper<Dataset, DatasetCreateDTO, DatasetResponseDTO> {

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Override
    public Dataset toEntity(DatasetCreateDTO datasetCreateDTO) {
        if (datasetCreateDTO == null) {
            return null;
        }

        return Dataset.builder()
                // Las relaciones se asignan en el Service
                .build();
    }

    @Override
    public DatasetCreateDTO toDTO(Dataset dataset) {
        if (dataset == null) {
            return null;
        }

        return DatasetCreateDTO.builder()
                .medicalDiagnosticId(dataset.getMedicalDiagnostic() != null ? dataset.getMedicalDiagnostic().getId() : null)
                .build();
    }
    @Override
    public DatasetResponseDTO toResponseDTO(Dataset dataset){
        if( dataset == null ){
            return null;
        }
        return DatasetResponseDTO.builder()
                .id(dataset.getId())
                .name(dataset.getName())
                .medicalDiagnostic(dataset.getMedicalDiagnostic() != null ? medicalDiagnosticMapper.toResponseDTO(dataset.getMedicalDiagnostic()) : null)
                .evaluationAreaId(dataset.getEvaluationArea() != null ? dataset.getEvaluationArea().getId() : null)
                .evaluationAreaName(dataset.getEvaluationArea() != null ? dataset.getEvaluationArea().getName() : null)
                .build();
    }
}
