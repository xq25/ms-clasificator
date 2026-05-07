package Backend.ms_clasificator.Mappers.MedicalDiagnosticMappers;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticCreateDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import org.springframework.stereotype.Component;

@Component
public class MedicalDiagnosticMapper implements Mapper<MedicalDiagnostic, MedicalDiagnosticCreateDTO> {
    @Override
    public MedicalDiagnostic toEntity(MedicalDiagnosticCreateDTO medicalDiagnosticCreateDTO) {
        if (medicalDiagnosticCreateDTO == null) {
            return null;
        }

        return MedicalDiagnostic.builder()
                .diagnosticCode(medicalDiagnosticCreateDTO.getDiagnosticCode())
                .diagnosticName(medicalDiagnosticCreateDTO.getDiagnosticName())
                // parentDiagnostic esta configurado en el service, para asegurar existencia por medio de validaciones
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
                // Mapear el ID del diagnóstico padre si existe
                .parentDiagnosticId(medicalDiagnostic.getParentDiagnostic() != null ?
                        medicalDiagnostic.getParentDiagnostic().getId() : null)
                .build();
    }
}

