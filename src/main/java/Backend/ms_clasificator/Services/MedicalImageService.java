package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgResponseDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Models.Patient;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import Backend.ms_clasificator.Repositories.MedicalImgRepository;
import Backend.ms_clasificator.Repositories.PatientRepository;
import Backend.ms_clasificator.Services.storage.ImageStorageService;
import Backend.ms_clasificator.exceptions.ImageStorageException;
import Backend.ms_clasificator.exceptions.InvalidImageTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service actualizado — usa ImageStoragePort (abstracción), no MinIO ni S3 directamente.
 *
 * FLUJO DE UPLOAD:
 * 1. Validar metadatos del DTO
 * 2. Subir imagen al storage (imageStoragePort.uploadImage) → retorna imageKey
 * 3. Persistir la entidad con el imageKey en MySQL
 * 4. Generar la URL de respuesta dinámicamente
 *
 * FLUJO DE DELETE:
 * 1. Buscar la entidad en MySQL
 * 2. Verificar que no tenga diagnósticos asignados
 * 3. Eliminar del storage
 * 4. Eliminar el registro de MySQL
 * → Si el delete del storage falla: no eliminamos el registro (consistencia)
 * → Si el delete del registro falla: la imagen queda "huérfana" en storage
 *   (esto se resuelve con un job de limpieza periódico, no aquí)
 *
 * RESPONSABILIDADES DE ESTE SERVICE:
 * - Orquestar la lógica de negocio
 * - NO sabe qué provider de storage está activo
 * - NO construye URLs directamente — las delega al port
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalImageService {

    private final MedicalImgRepository medicalImgRepository;
    private final EvaluationAreaRepository evaluationAreaRepository;
    private final PatientRepository patientRepository;
    private final ImageStorageService imageStorageService;  // ← INYECCIÓN POR INTERFAZ

    private static final String DEFAULT_FOLDER = "diagnostics";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    // =====================================================================
    // UPLOAD — operación principal
    // =====================================================================

    /**
     * Sube una imagen y crea el registro en BD.
     *
     * @param file   archivo de imagen recibido vía multipart
     * @param dto    metadatos de contexto (evaluationAreaId, patientId, folder)
     */
    @Transactional
    public ApiResponse<MedicalImgResponseDTO> uploadImage(MultipartFile file, MedicalImgCreateDTO dto) {
        try {
            if (file == null || file.isEmpty()) {
                return ApiResponse.error("El archivo de imagen no puede estar vacío");
            }

            // Validar que existe el área de evaluación
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(dto.getEvaluationAreaId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Área de evaluación no encontrada con ID: " + dto.getEvaluationAreaId()
                    ));

            // Validar paciente si viene
            if (dto.getPatientId() != null) {
                patientRepository.findById(dto.getPatientId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Paciente no encontrado con ID: " + dto.getPatientId()
                        ));
            }

            // Determinar carpeta de storage
            String folder = (dto.getFolder() != null && !dto.getFolder().isBlank())
                    ? dto.getFolder()
                    : DEFAULT_FOLDER;

            // 1. Subir al storage — si falla, la excepción se propaga y no toca la BD
            String imageKey = imageStorageService.uploadImage(file, folder);
            log.info("[MedicalImageService] Imagen subida. provider={}, key={}",
                    imageStorageService.getProviderName(), imageKey);

            // 2. Persistir en MySQL solo si el upload fue exitoso
            MedicalImg medicalImg = MedicalImg.builder()
                    .imageKey(imageKey)
                    .provider(imageStorageService.getProviderName())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .evaluationAreaId(evaluationArea.getId())
                    .patientId(dto.getPatientId())
                    .build();

            MedicalImg saved = medicalImgRepository.save(medicalImg);

            // 3. Construir respuesta con URL dinámica
            MedicalImgResponseDTO response = buildResponseDTO(saved);
            return ApiResponse.success(response, "Imagen médica subida exitosamente");

        } catch (InvalidImageTypeException ex) {
            return ApiResponse.error("Tipo de imagen no permitido: " + ex.getReceivedContentType());
        } catch (ImageStorageException ex) {
            log.error("[MedicalImageService] Error de storage [{}]: {}", ex.getProvider(), ex.getMessage());
            return ApiResponse.error("Error al subir imagen al storage: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            log.error("[MedicalImageService] Error inesperado en upload: {}", ex.getMessage(), ex);
            return ApiResponse.error("Error interno al procesar la imagen");
        }
    }

    // =====================================================================
    // LECTURAS
    // =====================================================================

    @Transactional(readOnly = true)
    public ApiResponse<MedicalImgResponseDTO> findById(Integer id) {
        try {
            MedicalImg img = medicalImgRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + id));
            return ApiResponse.success(buildResponseDTO(img), "Imagen médica obtenida exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al obtener imagen médica: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<MedicalImgResponseDTO>> findAll() {
        List<MedicalImgResponseDTO> dtos = medicalImgRepository.findAll()
                .stream()
                .map(this::buildResponseDTO)
                .toList();
        return ApiResponse.success(dtos, "Imágenes médicas obtenidas exitosamente");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<MedicalImgResponseDTO>> findByEvaluationAreaId(Integer evaluationAreaId) {
        try {
            evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Área de evaluación no encontrada con ID: " + evaluationAreaId
                    ));

            List<MedicalImgResponseDTO> dtos = medicalImgRepository.findByEvaluationAreaId(evaluationAreaId)
                    .stream()
                    .map(this::buildResponseDTO)
                    .toList();

            return ApiResponse.success(dtos, "Imágenes médicas obtenidas exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al obtener imágenes: " + ex.getMessage());
        }
    }

    // =====================================================================
    // DELETE — elimina del storage Y de la BD
    // =====================================================================

    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            MedicalImg img = medicalImgRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + id));

            // 1. Primero eliminar del storage
            imageStorageService.deleteImage(img.getImageKey());
            log.info("[MedicalImageService] Imagen eliminada del storage: key={}", img.getImageKey());

            // 2. Luego eliminar de la BD
            medicalImgRepository.delete(img);
            return ApiResponse.success("Imagen médica eliminada exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("No se puede eliminar: la imagen tiene diagnósticos asignados");
        } catch (ImageStorageException ex) {
            log.error("[MedicalImageService] Error eliminando del storage: {}", ex.getMessage());
            return ApiResponse.error("Error al eliminar imagen del storage. El registro no fue eliminado.");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar imagen médica: " + ex.getMessage());
        }
    }

    // =====================================================================
    // RELACIONES (sin cambios de lógica respecto al original)
    // =====================================================================

    @Transactional
    public ApiResponse<MedicalImgResponseDTO> assignPatient(Integer medicalImgId, Integer patientId) {
        try {
            MedicalImg img = medicalImgRepository.findById(medicalImgId)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + medicalImgId));

            patientRepository.findById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + patientId));

            if (img.getPatientId() != null) {
                throw new IllegalArgumentException("La imagen ya tiene paciente asignado. Remuévalo primero.");
            }

            img.setPatientId(patientId);
            return ApiResponse.success(buildResponseDTO(medicalImgRepository.save(img)),
                    "Paciente asignado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al asignar paciente: " + ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<MedicalImgResponseDTO> removePatient(Integer medicalImgId) {
        try {
            MedicalImg img = medicalImgRepository.findById(medicalImgId)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + medicalImgId));

            if (img.getPatientId() == null) {
                throw new IllegalArgumentException("La imagen no tiene paciente asignado.");
            }

            img.setPatientId(null);
            return ApiResponse.success(buildResponseDTO(medicalImgRepository.save(img)),
                    "Paciente removido exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al remover paciente: " + ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<MedicalImgResponseDTO> changeEvaluationArea(Integer medicalImgId, Integer evaluationAreaId) {
        try {
            MedicalImg img = medicalImgRepository.findById(medicalImgId)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + medicalImgId));

            evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + evaluationAreaId));

            img.setEvaluationAreaId(evaluationAreaId);
            return ApiResponse.success(buildResponseDTO(medicalImgRepository.save(img)),
                    "Área de evaluación cambiada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al cambiar área: " + ex.getMessage());
        }
    }

    // =====================================================================
    // HELPER — construye el DTO de respuesta con URL dinámica
    // =====================================================================

    /**
     * Genera la URL on-demand desde el storage port.
     * Nunca guardamos la URL en BD — siempre se genera aquí.
     */
    private MedicalImgResponseDTO buildResponseDTO(MedicalImg img) {
        String imageUrl = null;
        try {
            imageUrl = imageStorageService.generatePublicUrl(img.getImageKey());
        } catch (Exception e) {
            log.warn("[MedicalImageService] No se pudo generar URL para key={}: {}", img.getImageKey(), e.getMessage());
            // No fallamos la respuesta entera solo porque no se pudo generar la URL
        }

        return MedicalImgResponseDTO.builder()
                .id(img.getId())
                .imageKey(img.getImageKey())
                .imageUrl(imageUrl)
                .provider(img.getProvider())
                .contentType(img.getContentType())
                .fileSize(img.getFileSize())
                .evaluationAreaId(img.getEvaluationAreaId())
                .patientId(img.getPatientId())
                .createdAt(img.getCreatedAt())
                .build();
    }
}