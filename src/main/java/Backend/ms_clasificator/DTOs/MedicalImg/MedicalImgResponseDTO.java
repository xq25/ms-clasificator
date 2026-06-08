package Backend.ms_clasificator.DTOs.MedicalImg;

import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordSummaryDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para MedicalImg.
 * NOTA IMPORTANTE:
 * Incluye el campo `imageUrl` — una URL generada dinámicamente por el storage service.
 * Este DTO se construye en el service, NO desde el mapper directamente,
 * porque necesita llamar a imageStoragePort.generatePublicUrl(imageKey).
 * Nunca exponemos el imageKey al cliente si no es necesario.
 * Sí lo exponemos si el cliente necesita hacer operaciones futuras sobre él
 * (ej: el frontend de diagnóstico necesita saber qué imagen está viendo).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImgResponseDTO {

    private Integer id;
    private String imageKey;       // Clave en el storage — útil para el cliente avanzado
    private String imageUrl;       // URL generada on-demand — esta es la que el frontend usa
    private String provider;       // "minio" | "s3" | "r2"
    private String contentType;
    private MedicalImageTypeSummaryDTO medicalImageType;
    private Long fileSize;
    private LocalDateTime createdAt;
    private ClinicalRecordSummaryDTO clinicalRecord;
}