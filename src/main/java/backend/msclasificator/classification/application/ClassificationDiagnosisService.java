package backend.msclasificator.classification.application;

import backend.msclasificator.classification.api.dto.ClassificationDiagnosisCreateDTO;
import backend.msclasificator.classification.api.dto.ClassificationDiagnosisResponseDTO;
import backend.msclasificator.classification.api.dto.ClassificationDiagnosisSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.classification.api.mapper.ClassificationDiagnosisMapper;
import backend.msclasificator.classification.domain.ImageClassification;
import backend.msclasificator.classification.domain.ClassificationDiagnosis;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.classification.application.port.out.ClassificationDiagnosisRepository;
import backend.msclasificator.diagnosiscatalog.application.port.out.MedicalDiagnosticRepository;
import backend.msclasificator.dataset.application.port.out.CategoryDiagnosticRepository;
import backend.msclasificator.dataset.application.port.out.DatasetRepository;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.dataset.domain.DatasetCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassificationDiagnosisService {

    @Autowired
    private ClassificationDiagnosisRepository repository;

    @Autowired
    private ClassificationDiagnosisMapper mapper;

    @Autowired
    private ImageClassificationRepository imageDiagnosticRepository;

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private CategoryDiagnosticRepository categoryDiagnosticRepository;

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ClassificationDiagnosisSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<ClassificationDiagnosisSummaryDTO> page = repository.findAll(pageRequest.toPageQuery())
                .map(mapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<ClassificationDiagnosisSummaryDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Diagnósticos de imágenes obtenidos exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(repository.count(), "Total de diagnósticos de imágenes");
    }

    /**
     * Buscar por ID.
     */
    @Transactional(readOnly = true)
    public ApiResponse<ClassificationDiagnosisResponseDTO> findById(Integer id) {

        try {
            ClassificationDiagnosisResponseDTO diagnostic = repository.findById(id)
                    .map(mapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));

            return ApiResponse.success(diagnostic, "Diagnóstico encontrado");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ClassificationDiagnosisSummaryDTO>> findByImageDiagnosticId(Integer imageDiagnosticId, PageRequestDTO pageRequest) {
        try {
            if (!imageDiagnosticRepository.existsById(imageDiagnosticId)) {
                return ApiResponse.error("ImageClassification no encontrado con ID: " + imageDiagnosticId);
            }

            PageResult<ClassificationDiagnosisSummaryDTO> page = repository
                    .findByImageClassificationId(imageDiagnosticId, pageRequest.toPageQuery())
                    .map(mapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<ClassificationDiagnosisSummaryDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
                            .build(),
                    "Diagnósticos obtenidos exitosamente"
            );
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Crear asociación imagenDiagnostic-diagnóstico.
     */
    @Transactional
    public ApiResponse<ClassificationDiagnosisResponseDTO> create(ClassificationDiagnosisCreateDTO dto) {
        try {
            if (dto == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validamos que exista el diagnostico de imagen
            ImageClassification imageDiagnostic = imageDiagnosticRepository.findById(dto.getImageDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException("ImageClassification no encontrado con ID: " + dto.getImageDiagnosticId()));

            // Las clasificaciones de una imagen retirada quedan congeladas (ADR-013)
            if (imageDiagnostic.getMedicalImg() != null && imageDiagnostic.getMedicalImg().isRetired()) {
                return ApiResponse.error("La imagen médica " + imageDiagnostic.getMedicalImg().getId() + " está retirada");
            }

            // Validamos que exista el medical diagnostic
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(dto.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException("MedicalDiagnostic no encontrado con ID: " + dto.getMedicalDiagnosticId()));

            // Validar que no se le asigne un diagnostico repetido a la imagen
            if (repository.existsByImageClassificationIdAndMedicalDiagnosticId(imageDiagnostic.getId(), medicalDiagnostic.getId())){
                return ApiResponse.error("Este diagnóstico ya fue asignado a la imagen");
            }

            DatasetCategory categoriaClasificacion = imageDiagnostic.getDatasetCategory();
            if (categoriaClasificacion != null) {
                // Detalle opcional: el diagnóstico debe estar en la definición de la categoría vigente
                // cuando el doctor deslizó la imagen (ADR-010, ADR-011)
                boolean enDefinicion = categoryDiagnosticRepository
                        .findDefinitionAt(categoriaClasificacion.getId(), imageDiagnostic.getDiagnosticDate()).stream()
                        .anyMatch(cd -> cd.getMedicalDiagnostic().getId().equals(medicalDiagnostic.getId()));
                if (!enDefinicion) {
                    return ApiResponse.error("El diagnóstico no pertenece a la categoría '" + categoriaClasificacion.getName() + "' de la clasificación");
                }
            } else {
                // Clasificaciones previas a ADR-010 (sin categoría): reglas del bug #7.
                // El diagnóstico debe estar en una categoría del dataset del tipo de imagen,
                // y todos los diagnósticos de la clasificación deben ser de esa misma categoría.
                Integer medicalImageTypeId = imageDiagnostic.getMedicalImg().getMedicalImageType().getId();
                Dataset dataset = datasetRepository.findByMedicalImageTypeId(medicalImageTypeId).orElse(null);
                if (dataset == null) {
                    return ApiResponse.error("El tipo de imagen médica no tiene un dataset configurado");
                }

                DatasetCategory categoria = categoriaEnDataset(dataset, medicalDiagnostic.getId());
                if (categoria == null) {
                    return ApiResponse.error("El diagnóstico no pertenece a ninguna categoría del dataset '" + dataset.getName() + "'");
                }

                for (ClassificationDiagnosis existente : repository.findByImageClassificationId(imageDiagnostic.getId())) {
                    DatasetCategory categoriaExistente = categoriaEnDataset(dataset, existente.getMedicalDiagnostic().getId());
                    if (categoriaExistente == null) {
                        return ApiResponse.error("La imagen tiene diagnósticos que no pertenecen a ninguna categoría del dataset; no se pueden agregar más");
                    }
                    if (!categoriaExistente.getId().equals(categoria.getId())) {
                        return ApiResponse.error("La imagen ya está clasificada en la categoría '" + categoriaExistente.getName()
                                + "'; todos sus diagnósticos deben ser de la misma categoría");
                    }
                }
            }

            ClassificationDiagnosis entity = mapper.toEntity(dto);
            entity.setImageDiagnostic(imageDiagnostic);
            entity.setMedicalDiagnostic(medicalDiagnostic);

            return ApiResponse.success(mapper.toResponseDTO(repository.save(entity)), "Diagnóstico asignado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }


    /**
     * Eliminar asociación.
     */
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            ClassificationDiagnosis entity = repository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));

            // Las clasificaciones de una imagen retirada quedan congeladas (ADR-013)
            if (entity.getImageDiagnostic() != null && entity.getImageDiagnostic().getMedicalImg() != null
                    && entity.getImageDiagnostic().getMedicalImg().isRetired()) {
                return ApiResponse.error("La imagen médica " + entity.getImageDiagnostic().getMedicalImg().getId() + " está retirada");
            }

            repository.delete(entity);
            return ApiResponse.success("Diagnóstico eliminado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    private DatasetCategory categoriaEnDataset(Dataset dataset, Integer medicalDiagnosticId) {
        return categoryDiagnosticRepository.findByDatasetIdAndMedicalDiagnosticId(dataset.getId(), medicalDiagnosticId)
                .map(cd -> cd.getDatasetCategory())
                .orElse(null);
    }
}
