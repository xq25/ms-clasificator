package Backend.ms_clasificator.Mappers.ImageDiagnosticMappers;

import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.ImageDiagnostic;
import org.springframework.stereotype.Component;

@Component
public class ImageDiagnosticMapper implements Mapper<ImageDiagnostic, ImageDiagnosticCreateDTO, ImageDiagnosticResponseDTO> {
    @Override
    public ImageDiagnostic toEntity(ImageDiagnosticCreateDTO imageDiagnosticCreateDTO) {
        if (imageDiagnosticCreateDTO == null) {
            return null;
        }

        // Las relaciones y la fecha se asignan en el Service
        return ImageDiagnostic.builder()
                .build();
    }

    @Override
    public ImageDiagnosticCreateDTO toDTO(ImageDiagnostic imageDiagnostic) {
        if (imageDiagnostic == null) {
            return null;
        }

        return ImageDiagnosticCreateDTO.builder()
                .doctorId(imageDiagnostic.getDoctor() != null ? imageDiagnostic.getDoctor().getId() : null)
                .medicalImgId(imageDiagnostic.getMedicalImg() != null ? imageDiagnostic.getMedicalImg().getId() : null)
                .build();
    }

    @Override
    public ImageDiagnosticResponseDTO toResponseDTO(ImageDiagnostic imageDiagnostic){
        if (imageDiagnostic == null){
            return null;
        }

        return ImageDiagnosticResponseDTO.builder()
                .id(imageDiagnostic.getId())
                .doctorId(imageDiagnostic.getDoctorId())
                .medicalImgId(imageDiagnostic.getMedicalImgId())
                .diagnosticDate(imageDiagnostic.getDiagnosticDate())
                .build();
    }
}

