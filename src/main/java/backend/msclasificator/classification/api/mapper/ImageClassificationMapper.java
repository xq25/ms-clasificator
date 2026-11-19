package backend.msclasificator.classification.api.mapper;

import backend.msclasificator.classification.api.dto.ImageClassificationCreateDTO;
import backend.msclasificator.classification.api.dto.ImageClassificationResponseDTO;
import backend.msclasificator.classification.api.dto.ImageClassificationSummaryDTO;
import backend.msclasificator.doctor.api.mapper.DoctorMapper;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.medicalimage.api.mapper.MedicalImageMapper;
import backend.msclasificator.classification.domain.ImageClassification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageClassificationMapper implements Mapper<ImageClassification, ImageClassificationCreateDTO, ImageClassificationResponseDTO, ImageClassificationSummaryDTO> {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private MedicalImageMapper medicalImgMapper;

    @Override
    public ImageClassification toEntity(ImageClassificationCreateDTO imageDiagnosticCreateDTO) {
        if (imageDiagnosticCreateDTO == null) {
            return null;
        }

        // Las relaciones y la fecha se asignan en el Service
        return ImageClassification.builder()
                .build();
    }

    @Override
    public ImageClassificationCreateDTO toDTO(ImageClassification imageDiagnostic) {
        if (imageDiagnostic == null) {
            return null;
        }

        return ImageClassificationCreateDTO.builder()
                .doctorId(imageDiagnostic.getDoctor() != null ? imageDiagnostic.getDoctor().getId() : null)
                .medicalImgId(imageDiagnostic.getMedicalImg() != null ? imageDiagnostic.getMedicalImg().getId() : null)
                .build();
    }

    @Override
    public ImageClassificationResponseDTO toResponseDTO(ImageClassification imageDiagnostic) {
        if (imageDiagnostic == null) {
            return null;
        }

        return ImageClassificationResponseDTO.builder()
                .id(imageDiagnostic.getId())
                .doctor(imageDiagnostic.getDoctor() != null ? doctorMapper.toSummaryDTO(imageDiagnostic.getDoctor()): null)
                .medicalImg(imageDiagnostic.getMedicalImg() != null ? medicalImgMapper.toSummaryDTO(imageDiagnostic.getMedicalImg()) : null)
                .diagnosticDate(imageDiagnostic.getDiagnosticDate())
                .build();
    }

    @Override
    public ImageClassificationSummaryDTO toSummaryDTO(ImageClassification imageDiagnostic) {
        if (imageDiagnostic == null) {
            return null;
        }

        return ImageClassificationSummaryDTO.builder()
                .id(imageDiagnostic.getId())
                .doctorId(imageDiagnostic.getDoctor() != null ? imageDiagnostic.getDoctor().getId() : null)
                .medicalImageId(imageDiagnostic.getMedicalImg() != null ? imageDiagnostic.getMedicalImg().getId() : null)
                .diagnosticDate(imageDiagnostic.getDiagnosticDate())
                .build();
    }
}