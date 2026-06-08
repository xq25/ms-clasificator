package Backend.ms_clasificator.Mappers.ImageDiagnosticMappers;

import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticResponseDTO;
import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticSummaryDTO;
import Backend.ms_clasificator.Mappers.DoctorMappers.DoctorMapper;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Mappers.MedicalImgMappers.MedicalImgMapper;
import Backend.ms_clasificator.Models.ImageDiagnostic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageDiagnosticMapper implements Mapper<ImageDiagnostic, ImageDiagnosticCreateDTO, ImageDiagnosticResponseDTO, ImageDiagnosticSummaryDTO> {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private MedicalImgMapper medicalImgMapper;

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
    public ImageDiagnosticResponseDTO toResponseDTO(ImageDiagnostic imageDiagnostic) {
        if (imageDiagnostic == null) {
            return null;
        }

        return ImageDiagnosticResponseDTO.builder()
                .id(imageDiagnostic.getId())
                .doctor(imageDiagnostic.getDoctor() != null ? doctorMapper.toSummaryDTO(imageDiagnostic.getDoctor()): null)
                .medicalImg(imageDiagnostic.getMedicalImg() != null ? medicalImgMapper.toSummaryDTO(imageDiagnostic.getMedicalImg()) : null)
                .diagnosticDate(imageDiagnostic.getDiagnosticDate())
                .build();
    }

    @Override
    public ImageDiagnosticSummaryDTO toSummaryDTO(ImageDiagnostic imageDiagnostic) {
        if (imageDiagnostic == null) {
            return null;
        }

        return ImageDiagnosticSummaryDTO.builder()
                .id(imageDiagnostic.getId())
                .doctorId(imageDiagnostic.getDoctor() != null ? imageDiagnostic.getDoctor().getId() : null)
                .medicalImageId(imageDiagnostic.getMedicalImg() != null ? imageDiagnostic.getMedicalImg().getId() : null)
                .diagnosticDate(imageDiagnostic.getDiagnosticDate())
                .build();
    }
}