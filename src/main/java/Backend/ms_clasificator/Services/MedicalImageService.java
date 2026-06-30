package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgResponseDTO;
import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgSummaryDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import org.springframework.data.domain.Page;
import Backend.ms_clasificator.Mappers.MedicalImgMappers.MedicalImgMapper;
import Backend.ms_clasificator.Models.ClinicalRecord;
import Backend.ms_clasificator.Models.MedicalImageType;
import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Repositories.ClinicalRecordRepository;
import Backend.ms_clasificator.Repositories.ImageDiagnosticRepository;
import Backend.ms_clasificator.Repositories.MedicalImageTypeRepository;
import Backend.ms_clasificator.Repositories.MedicalImgRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalImageService {

    private final MedicalImgRepository medicalImgRepository;
    private final MedicalImageTypeRepository medicalImageTypeRepository;
    private final ImageDiagnosticRepository imageDiagnosticRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final ImageStorageService imageStorageService;
    private final MedicalImgMapper medicalImgMapper;

    private static final String DEFAULT_FOLDER = "diagnostics";

    // UPLOAD
    @Transactional
    public ApiResponse<MedicalImgResponseDTO> uploadImage(MultipartFile file, MedicalImgCreateDTO dto) {
        try {
            if (file == null || file.isEmpty()) {
                return ApiResponse.error("El archivo de imagen no puede estar vacío");
            }

            MedicalImageType medicalImageType = medicalImageTypeRepository.findById(dto.getMedicalImageTypeId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Tipo de imagen medica no encontrada con ID: " + dto.getMedicalImageTypeId()
                    ));

            ClinicalRecord clinicalRecord = null;
            if (dto.getClinicalRecordId() != null) {
                clinicalRecord = clinicalRecordRepository.findById(dto.getClinicalRecordId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Clinical record no encontrado con ID: " + dto.getClinicalRecordId()
                        ));
            }

            String folder = (dto.getFolder() != null && !dto.getFolder().isBlank())
                    ? dto.getFolder()
                    : DEFAULT_FOLDER;

            // 1. Subir al storage — si falla, no toca la BD
            String imageKey = imageStorageService.uploadImage(file, folder);
            log.info("[MedicalImageService] Imagen subida. provider={}, key={}",
                    imageStorageService.getProviderName(), imageKey);

            // 2. Persistir en MySQL solo si el upload fue exitoso
            MedicalImg medicalImg = MedicalImg.builder()
                    .imageKey(imageKey)
                    .provider(imageStorageService.getProviderName())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .medicalImageType(medicalImageType)
                    .clinicalRecord(clinicalRecord)
                    .build();

            MedicalImg saved = medicalImgRepository.save(medicalImg);

            // 3. Delegar la construcción del DTO al mapper
            return ApiResponse.success(medicalImgMapper.toResponseDTO(saved), "Imagen médica subida exitosamente");

        } catch (InvalidImageTypeException ex) {
            return ApiResponse.error("Tipo de imagen no permitido: " + ex.getReceivedContentType());
        } catch (ImageStorageException ex) {
            log.error("[MedicalImageService] Error de storage [{}]: {}", ex.getProvider(), ex.getMessage());
            return ApiResponse.error("Error al subir imagen al storage: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    // LECTURAS
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalImgSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        Page<MedicalImgSummaryDTO> page = medicalImgRepository.findAll(pageRequest.toPageable())
                .map(medicalImgMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<MedicalImgSummaryDTO>builder()
                        .content(page.getContent())
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .last(page.isLast())
                        .build(),
                "Imágenes médicas obtenidas exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(medicalImgRepository.countAll(), "Total de imágenes médicas");
    }

    @Transactional(readOnly = true)
    public ApiResponse<MedicalImgResponseDTO> findById(Integer id) {
        try {
            MedicalImgResponseDTO img = medicalImgRepository.findById(id)
                    .map(medicalImgMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + id));
            return ApiResponse.success(img, "Imagen médica obtenida exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalImgResponseDTO>> findByMedicalImageType(Integer medicalImageTypeId, PageRequestDTO pageRequest) {
        try {
            if (!medicalImageTypeRepository.existsById(medicalImageTypeId)) {
                return ApiResponse.error("No se encontró el tipo de imagen médica con ID: " + medicalImageTypeId);
            }

            Page<MedicalImgResponseDTO> page = medicalImgRepository
                    .findByMedicalImageTypeId(medicalImageTypeId, pageRequest.toPageable())
                    .map(medicalImgMapper::toResponseDTO);

            return ApiResponse.success(
                    PagedResponse.<MedicalImgResponseDTO>builder()
                            .content(page.getContent())
                            .page(page.getNumber())
                            .size(page.getSize())
                            .totalElements(page.getTotalElements())
                            .totalPages(page.getTotalPages())
                            .last(page.isLast())
                            .build(),
                    "Imágenes médicas obtenidas exitosamente"
            );
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error("Error al obtener imágenes médicas por tipo: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalImgSummaryDTO>> findByClinicalRecord(Integer clinicalRecordId, PageRequestDTO pageRequest) {
        try {
            if (!clinicalRecordRepository.existsById(clinicalRecordId)) {
                return ApiResponse.error("No se encontró el registro clínico con ID: " + clinicalRecordId);
            }

            Page<MedicalImgSummaryDTO> page = medicalImgRepository
                    .findByClinicalRecordId(clinicalRecordId, pageRequest.toPageable())
                    .map(medicalImgMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<MedicalImgSummaryDTO>builder()
                            .content(page.getContent())
                            .page(page.getNumber())
                            .size(page.getSize())
                            .totalElements(page.getTotalElements())
                            .totalPages(page.getTotalPages())
                            .last(page.isLast())
                            .build(),
                    page.isEmpty()
                            ? "No se encontraron imágenes médicas para este clinical record"
                            : "Imágenes médicas obtenidas exitosamente"
            );
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error("Error al obtener imágenes médicas por registro clínico: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<MedicalImgResponseDTO>> findUndiagnosedByDoctorAndMedicalImageType(Integer doctorId, Integer medicalImageTypeId) {
        try {
            if(!medicalImageTypeRepository.existsById(medicalImageTypeId)){
                return ApiResponse.error("No se encontró el tipo de imagen médica con ID: " + medicalImageTypeId);
            }

            List<MedicalImgResponseDTO> dtos = medicalImgRepository
                    .findUndiagnosedImagesByDoctorAndMedicalImageType(doctorId, medicalImageTypeId)
                    .stream()
                    .map(medicalImgMapper::toResponseDTO)
                    .toList();

            return ApiResponse.success(dtos, "Imágenes médicas no diagnosticadas obtenidas exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error("Error al obtener imágenes médicas no diagnosticadas: " + ex.getMessage());
        }
    }

    // DELETE
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            MedicalImg img = medicalImgRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + id));

            // Validamos que la imagen no tenga diagnosticos asociados
            if(imageDiagnosticRepository.existsByMedicalImgId(id)) {
                return ApiResponse.error("No se puede eliminar: la imagen tiene diagnósticos asignados");
            }

            // 1. Eliminar del storage primero
            imageStorageService.deleteImage(img.getImageKey());
            log.info("[MedicalImageService] Imagen eliminada del storage: key={}", img.getImageKey());

            // 2. Eliminar de la BD solo si el storage no falló
            medicalImgRepository.delete(img);
            return ApiResponse.success("Imagen médica eliminada exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violacion a la integridad de la base de datos:" + ex.getMessage());
        } catch (ImageStorageException ex) {
            log.error("[MedicalImageService] Error eliminando del storage: {}", ex.getMessage());
            return ApiResponse.error("Error al eliminar imagen del storage. El registro no fue eliminado.");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }
}