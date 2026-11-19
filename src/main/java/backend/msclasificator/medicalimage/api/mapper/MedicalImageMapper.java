package backend.msclasificator.medicalimage.api.mapper;

import backend.msclasificator.medicalimage.api.dto.MedicalImageCreateDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageResponseDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageSummaryDTO;
import backend.msclasificator.clinicalrecord.api.mapper.ClinicalRecordMapper;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.medicalimage.api.mapper.MedicalImageTypeMapper;
import backend.msclasificator.medicalimage.domain.MedicalImage;
import backend.msclasificator.medicalimage.application.port.out.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicalImageMapper implements Mapper<MedicalImage, MedicalImageCreateDTO, MedicalImageResponseDTO, MedicalImageSummaryDTO> {

    @Autowired
    private MedicalImageTypeMapper medicalImageTypeMapper;

    @Autowired
    private ClinicalRecordMapper clinicalRecordMapper;

    private final ImageStorageService imageStorageService;

    @Override
    public MedicalImage toEntity(MedicalImageCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        // El archivo, imageKey, provider, contentType y fileSize
        // se asignan en el Service tras el upload al storage
        return MedicalImage.builder()
                .build();
    }

    @Override
    public MedicalImageCreateDTO toDTO(MedicalImage entity) {
        if (entity == null) {
            return null;
        }

        return MedicalImageCreateDTO.builder()
                .medicalImageTypeId(entity.getMedicalImageType() != null ? entity.getMedicalImageType().getId() : null)
                .clinicalRecordId(entity.getClinicalRecord() != null ? entity.getClinicalRecord().getId() : null)
                .build();
    }

    /**
     * Construye el DTO completo de respuesta.
     * Llama al storage service para generar la URL on-demand.
     * Si la generación de URL falla, devuelve el DTO igualmente con imageUrl = null
     * para no romper la respuesta entera por un fallo de URL.
     */
    @Override
    public MedicalImageResponseDTO toResponseDTO(MedicalImage entity) {
        if (entity == null) {
            return null;
        }

        String imageUrl = null;
        try {
            imageUrl = imageStorageService.generatePublicUrl(entity.getImageKey());
        } catch (Exception e) {
            log.warn("[MedicalImageMapper] No se pudo generar URL para key={}: {}", entity.getImageKey(), e.getMessage());
        }

        return MedicalImageResponseDTO.builder()
                .id(entity.getId())
                .imageKey(entity.getImageKey())
                .imageUrl(imageUrl)
                .provider(entity.getProvider())
                .contentType(entity.getContentType())
                .fileSize(entity.getFileSize())
                .medicalImageType(entity.getMedicalImageType() != null ? medicalImageTypeMapper.toSummaryDTO(entity.getMedicalImageType()) : null)
                .clinicalRecord(entity.getClinicalRecord() != null ? clinicalRecordMapper.toSummaryDTO(entity.getClinicalRecord()) : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Solo campos propios de la entidad, sin relaciones ni URL dinámica.
     * Útil para listados livianos o referencias internas donde la URL no es necesaria.
     */
    @Override
    public MedicalImageSummaryDTO toSummaryDTO(MedicalImage entity) {
        if (entity == null) {
            return null;
        }

        return MedicalImageSummaryDTO.builder()
                .id(entity.getId())
                .imageKey(entity.getImageKey())
                .provider(entity.getProvider())
                .contentType(entity.getContentType())
                .fileSize(entity.getFileSize())
                .medicalImageTypeName(entity.getMedicalImageType() != null ? entity.getMedicalImageType().getName() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}