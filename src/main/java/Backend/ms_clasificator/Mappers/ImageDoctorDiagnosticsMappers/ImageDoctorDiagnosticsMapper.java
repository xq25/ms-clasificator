package Backend.ms_clasificator.Mappers.ImageDoctorDiagnosticsMappers;

import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsResponseDTO;
import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsSummaryDTO;
import Backend.ms_clasificator.Mappers.ImageDiagnosticMappers.ImageDiagnosticMapper;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Mappers.MedicalDiagnosticMappers.MedicalDiagnosticMapper;
import Backend.ms_clasificator.Models.ImageDoctorDiagnostics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageDoctorDiagnosticsMapper implements Mapper<ImageDoctorDiagnostics, ImageDoctorDiagnosticsCreateDTO, ImageDoctorDiagnosticsResponseDTO, ImageDoctorDiagnosticsSummaryDTO> {

    @Autowired
    private ImageDiagnosticMapper imageDiagnosticMapper;

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Override
    public ImageDoctorDiagnostics toEntity(ImageDoctorDiagnosticsCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return ImageDoctorDiagnostics.builder()
                .build();
    }

    @Override
    public ImageDoctorDiagnosticsCreateDTO toDTO(ImageDoctorDiagnostics entity) {
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
    public ImageDoctorDiagnosticsResponseDTO toResponseDTO(ImageDoctorDiagnostics entity) {
        if (entity == null) {
            return null;
        }

        return ImageDoctorDiagnosticsResponseDTO.builder()
                .id(entity.getId())
                .imageDiagnostic(entity.getImageDiagnostic() != null ? imageDiagnosticMapper.toSummaryDTO(entity.getImageDiagnostic()) : null)
                .medicalDiagnostic(entity.getMedicalDiagnostic() != null ? medicalDiagnosticMapper.toSummaryDTO(entity.getMedicalDiagnostic()) : null)
                .build();
    }

    @Override
    public ImageDoctorDiagnosticsSummaryDTO toSummaryDTO(ImageDoctorDiagnostics entity) {
        if (entity == null) {
            return null;
        }

        return ImageDoctorDiagnosticsSummaryDTO.builder()
                .id(entity.getId())
                .imageDiagnosticId(entity.getImageDiagnostic() != null ? entity.getImageDiagnostic().getId() : null)
                .medicalDiagnostic(entity.getMedicalDiagnostic() != null ? medicalDiagnosticMapper.toSummaryDTO(entity.getMedicalDiagnostic()) : null)
                .build();
    }
}