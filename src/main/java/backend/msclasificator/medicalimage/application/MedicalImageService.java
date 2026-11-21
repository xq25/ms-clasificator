package backend.msclasificator.medicalimage.application;

import backend.msclasificator.medicalimage.api.dto.MedicalImageCreateDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageResponseDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.medicalimage.api.mapper.MedicalImageMapper;
import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
import backend.msclasificator.medicalimage.domain.MedicalImageType;
import backend.msclasificator.medicalimage.domain.MedicalImage;
import backend.msclasificator.clinicalrecord.application.port.out.ClinicalRecordRepository;
import backend.msclasificator.classification.infrastructure.persistence.ImageClassificationRepository;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageTypeRepository;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageRepository;
import backend.msclasificator.medicalimage.application.port.out.ImageStorageService;
import backend.msclasificator.medicalimage.application.exception.ImageStorageException;
import backend.msclasificator.medicalimage.application.exception.InvalidImageTypeException;
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

    private final MedicalImageRepository medicalImgRepository;
    private final MedicalImageTypeRepository medicalImageTypeRepository;
    private final ImageClassificationRepository imageDiagnosticRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final ImageStorageService imageStorageService;
    private final MedicalImageMapper medicalImgMapper;

    private static final String DEFAULT_FOLDER = "diagnostics";

    // UPLOAD
    @Transactional
    public ApiResponse<MedicalImageResponseDTO> uploadImage(MultipartFile file, MedicalImageCreateDTO dto) {
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
            MedicalImage medicalImg = MedicalImage.builder()
                    .imageKey(imageKey)
                    .provider(imageStorageService.getProviderName())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .medicalImageType(medicalImageType)
                    .clinicalRecord(clinicalRecord)
                    .build();

            MedicalImage saved = medicalImgRepository.save(medicalImg);

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
    public ApiResponse<PagedResponse<MedicalImageSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<MedicalImageSummaryDTO> page = medicalImgRepository.findAll(pageRequest.toPageQuery())
                .map(medicalImgMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<MedicalImageSummaryDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Imágenes médicas obtenidas exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(medicalImgRepository.count(), "Total de imágenes médicas");
    }

    @Transactional(readOnly = true)
    public ApiResponse<MedicalImageResponseDTO> findById(Integer id) {
        try {
            MedicalImageResponseDTO img = medicalImgRepository.findById(id)
                    .map(medicalImgMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + id));
            return ApiResponse.success(img, "Imagen médica obtenida exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalImageResponseDTO>> findByMedicalImageType(Integer medicalImageTypeId, PageRequestDTO pageRequest) {
        try {
            if (!medicalImageTypeRepository.existsById(medicalImageTypeId)) {
                return ApiResponse.error("No se encontró el tipo de imagen médica con ID: " + medicalImageTypeId);
            }

            PageResult<MedicalImageResponseDTO> page = medicalImgRepository
                    .findByMedicalImageTypeId(medicalImageTypeId, pageRequest.toPageQuery())
                    .map(medicalImgMapper::toResponseDTO);

            return ApiResponse.success(
                    PagedResponse.<MedicalImageResponseDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
                            .build(),
                    "Imágenes médicas obtenidas exitosamente"
            );
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error("Error al obtener imágenes médicas por tipo: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalImageSummaryDTO>> findByClinicalRecord(Integer clinicalRecordId, PageRequestDTO pageRequest) {
        try {
            if (!clinicalRecordRepository.existsById(clinicalRecordId)) {
                return ApiResponse.error("No se encontró el registro clínico con ID: " + clinicalRecordId);
            }

            PageResult<MedicalImageSummaryDTO> page = medicalImgRepository
                    .findByClinicalRecordId(clinicalRecordId, pageRequest.toPageQuery())
                    .map(medicalImgMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<MedicalImageSummaryDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
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
    public ApiResponse<List<MedicalImageResponseDTO>> findUndiagnosedByDoctorAndMedicalImageType(Integer doctorId, Integer medicalImageTypeId) {
        try {
            if(!medicalImageTypeRepository.existsById(medicalImageTypeId)){
                return ApiResponse.error("No se encontró el tipo de imagen médica con ID: " + medicalImageTypeId);
            }

            List<MedicalImageResponseDTO> dtos = medicalImgRepository
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
            MedicalImage img = medicalImgRepository.findById(id)
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