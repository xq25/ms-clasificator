package Backend.ms_clasificator.Mappers.ImageDoctorDiagnosticsMappers;

import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.ImageDoctorDiagnostics;
import org.springframework.stereotype.Component;

@Component
public class ImageDoctorDiagnosticsMapper implements
        Mapper<
                ImageDoctorDiagnostics,
                ImageDoctorDiagnosticsCreateDTO,
                ImageDoctorDiagnosticsResponseDTO> {

    @Override
    public ImageDoctorDiagnostics toEntity(
            ImageDoctorDiagnosticsCreateDTO dto) {

        if (dto == null) {
            return null;
        }

        return ImageDoctorDiagnostics.builder()
                .build();
    }

    @Override
    public ImageDoctorDiagnosticsCreateDTO toDTO(
            ImageDoctorDiagnostics entity) {

        if (entity == null) {
            return null;
        }

        return ImageDoctorDiagnosticsCreateDTO.builder()
                .imageDiagnosticId(
                        entity.getImageDiagnostic() != null
                                ? entity.getImageDiagnostic().getId()
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
    public ImageDoctorDiagnosticsResponseDTO toResponseDTO(
            ImageDoctorDiagnostics entity) {

        if (entity == null) {
            return null;
        }

        return ImageDoctorDiagnosticsResponseDTO.builder()
                .id(entity.getId())
                .imageDiagnosticId(
                        entity.getImageDiagnostic() != null
                                ? entity.getImageDiagnostic().getId()
                                : null
                )
                .medicalDiagnosticId(
                        entity.getMedicalDiagnostic() != null
                                ? entity.getMedicalDiagnostic().getId()
                                : null
                )
                .medicalDiagnosticName(
                        entity.getMedicalDiagnostic() != null
                                ? entity.getMedicalDiagnostic().getDiagnosticName()
                                : null
                )
                .build();
    }
}
