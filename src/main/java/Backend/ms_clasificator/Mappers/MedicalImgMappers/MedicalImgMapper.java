package Backend.ms_clasificator.Mappers.MedicalImgMappers;

import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgResponseDTO;
import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgSummaryDTO;
import Backend.ms_clasificator.Mappers.ClinicalRecordMappers.ClinicalRecordMapper;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Mappers.MedicalImageTypeMappers.MedicalImageTypeMapper;
import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Services.storage.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicalImgMapper implements Mapper<MedicalImg, MedicalImgCreateDTO, MedicalImgResponseDTO, MedicalImgSummaryDTO> {

    @Autowired
    private MedicalImageTypeMapper medicalImageTypeMapper;

    @Autowired
    private ClinicalRecordMapper clinicalRecordMapper;

    private final ImageStorageService imageStorageService;

    @Override
    public MedicalImg toEntity(MedicalImgCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        // El archivo, imageKey, provider, contentType y fileSize
        // se asignan en el Service tras el upload al storage
        return MedicalImg.builder()
                .build();
    }

    @Override
    public MedicalImgCreateDTO toDTO(MedicalImg entity) {
        if (entity == null) {
            return null;
        }

        return MedicalImgCreateDTO.builder()
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
    public MedicalImgResponseDTO toResponseDTO(MedicalImg entity) {
        if (entity == null) {
            return null;
        }

        String imageUrl = null;
        try {
            imageUrl = imageStorageService.generatePublicUrl(entity.getImageKey());
        } catch (Exception e) {
            log.warn("[MedicalImgMapper] No se pudo generar URL para key={}: {}", entity.getImageKey(), e.getMessage());
        }

        return MedicalImgResponseDTO.builder()
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
    public MedicalImgSummaryDTO toSummaryDTO(MedicalImg entity) {
        if (entity == null) {
            return null;
        }

        return MedicalImgSummaryDTO.builder()
                .id(entity.getId())
                .imageKey(entity.getImageKey())
                .provider(entity.getProvider())
                .contentType(entity.getContentType())
                .fileSize(entity.getFileSize())
                .medicalImageType(entity.getMedicalImageType() != null ? entity.getMedicalImageType().getName() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}