package Backend.ms_clasificator.Mappers.MedicalDiagnosticMappers;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticResponseDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticSummaryDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import org.springframework.stereotype.Component;

@Component
public class MedicalDiagnosticMapper implements Mapper<MedicalDiagnostic, MedicalDiagnosticCreateDTO, MedicalDiagnosticResponseDTO, MedicalDiagnosticSummaryDTO> {

    @Override
    public MedicalDiagnostic toEntity(MedicalDiagnosticCreateDTO medicalDiagnosticCreateDTO) {
        if (medicalDiagnosticCreateDTO == null) {
            return null;
        }

        return MedicalDiagnostic.builder()
                .diagnosticCode(medicalDiagnosticCreateDTO.getDiagnosticCode())
                .diagnosticName(medicalDiagnosticCreateDTO.getDiagnosticName())
                // parentDiagnostic se configura en el service para asegurar existencia por medio de validaciones
                .build();
    }

    @Override
    public MedicalDiagnosticCreateDTO toDTO(MedicalDiagnostic medicalDiagnostic) {
        if (medicalDiagnostic == null) {
            return null;
        }

        return MedicalDiagnosticCreateDTO.builder()
                .diagnosticCode(medicalDiagnostic.getDiagnosticCode())
                .diagnosticName(medicalDiagnostic.getDiagnosticName())
                .parentDiagnosticId(medicalDiagnostic.getParentDiagnostic() != null ? medicalDiagnostic.getParentDiagnostic().getId() : null)
                .build();
    }

    @Override
    public MedicalDiagnosticResponseDTO toResponseDTO(MedicalDiagnostic medicalDiagnostic) {
        if (medicalDiagnostic == null) {
            return null;
        }

        return MedicalDiagnosticResponseDTO.builder()
                .id(medicalDiagnostic.getId())
                .diagnosticName(medicalDiagnostic.getDiagnosticName())
                .diagnosticCode(medicalDiagnostic.getDiagnosticCode())
                .parentDiagnosticId(medicalDiagnostic.getParentDiagnostic() != null ? medicalDiagnostic.getParentDiagnostic().getId() : null)
                .parentDiagnosticCode(medicalDiagnostic.getParentDiagnostic() != null ? medicalDiagnostic.getDiagnosticCode() : null) // El codigo lo definimos dentro del service
                .build();
    }

    @Override
    public MedicalDiagnosticSummaryDTO toSummaryDTO(MedicalDiagnostic medicalDiagnostic) {
        if (medicalDiagnostic == null) {
            return null;
        }

        return MedicalDiagnosticSummaryDTO.builder()
                .id(medicalDiagnostic.getId())
                .diagnosticCode(medicalDiagnostic.getDiagnosticCode())
                .diagnosticName(medicalDiagnostic.getDiagnosticName())
                .build();
    }
}