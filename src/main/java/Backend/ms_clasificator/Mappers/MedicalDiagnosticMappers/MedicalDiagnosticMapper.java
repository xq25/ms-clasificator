package Backend.ms_clasificator.Mappers.MedicalDiagnosticMappers;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticCreateDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.MedicalDiagnostic;

public class MedicalDiagnosticMapper implements Mapper<MedicalDiagnostic, MedicalDiagnosticCreateDTO> {
    @Override
    public MedicalDiagnostic toEntity(MedicalDiagnosticCreateDTO medicalDiagnosticCreateDTO) {
        if (medicalDiagnosticCreateDTO == null) {
            return null;
        }

        return MedicalDiagnostic.builder()
                .diagnosticCode(medicalDiagnosticCreateDTO.getDiagnosticCode())
                .diagnosticName(medicalDiagnosticCreateDTO.getDiagnosticName())
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
                .build();
    }
}

