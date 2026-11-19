package backend.msclasificator.diagnosiscatalog.api.mapper;

import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticCreateDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticResponseDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
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
                .parentDiagnostic(medicalDiagnostic.getParentDiagnostic() != null ? toSummaryDTO(medicalDiagnostic.getParentDiagnostic()) : null)
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