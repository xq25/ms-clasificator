package backend.msclasificator.classification.application;

import backend.msclasificator.classification.api.dto.ImageClassificationCreateDTO;
import backend.msclasificator.classification.api.dto.ImageClassificationResponseDTO;
import backend.msclasificator.classification.api.dto.ImageClassificationSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.classification.api.mapper.ImageClassificationMapper;
import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.classification.domain.ImageClassification;
import backend.msclasificator.medicalimage.domain.MedicalImage;
import backend.msclasificator.doctor.application.port.out.DoctorAreaRepository;
import backend.msclasificator.doctor.application.port.out.DoctorRepository;
import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageRepository;
import backend.msclasificator.dataset.application.port.out.CategoryDiagnosticRepository;
import backend.msclasificator.dataset.application.port.out.DatasetCategoryRepository;
import backend.msclasificator.dataset.application.port.out.DatasetRepository;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.diagnosiscatalog.api.mapper.MedicalDiagnosticMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImageClassificationService {

    @Autowired
    private ImageClassificationRepository imageDiagnosticRepository;

    @Autowired
    private ImageClassificationMapper imageDiagnosticMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private MedicalImageRepository medicalImgRepository;

    @Autowired
    private DoctorAreaRepository doctorAreaRepository;

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private DatasetCategoryRepository datasetCategoryRepository;

    @Autowired
    private CategoryDiagnosticRepository categoryDiagnosticRepository;

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ImageClassificationSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<ImageClassificationSummaryDTO> page = imageDiagnosticRepository.findAll(pageRequest.toPageQuery())
                .map(imageDiagnosticMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<ImageClassificationSummaryDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Diagnósticos de imagen obtenidos exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(imageDiagnosticRepository.count(), "Total de diagnósticos de imagen");
    }

    @Transactional(readOnly = true)
    public ApiResponse<ImageClassificationResponseDTO> findById(Integer id) {
        try {
            ImageClassificationResponseDTO imageDiagnostic = imageDiagnosticRepository.findById(id)
                    .map(this::toResponse)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));

            return ApiResponse.success(imageDiagnostic, "Diagnóstico de imagen encontrado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ImageClassificationSummaryDTO>> findByDoctorId(Integer doctorId, PageRequestDTO pageRequest) {
        try {
            PageResult<ImageClassificationSummaryDTO> page = imageDiagnosticRepository
                    .findByDoctorId(doctorId, pageRequest.toPageQuery())
                    .map(imageDiagnosticMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<ImageClassificationSummaryDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
                            .build(),
                    page.isEmpty()
                            ? "No se encontraron diagnósticos de imagen para el doctor con ID: " + doctorId
                            : "Diagnósticos de imagen encontrados exitosamente para el doctor con ID: " + doctorId
            );
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ImageClassificationSummaryDTO>> findByMedicalImgId(Integer medicalImgId, PageRequestDTO pageRequest) {
        try {
            PageResult<ImageClassificationSummaryDTO> page = imageDiagnosticRepository
                    .findByMedicalImageId(medicalImgId, pageRequest.toPageQuery())
                    .map(imageDiagnosticMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<ImageClassificationSummaryDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
                            .build(),
                    page.isEmpty()
                            ? "No se encontraron diagnósticos de imagen para la imagen médica con ID: " + medicalImgId
                            : "Diagnósticos de imagen encontrados exitosamente para la imagen médica con ID: " + medicalImgId
            );
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<ImageClassificationResponseDTO> create(ImageClassificationCreateDTO imageDiagnosticCreateDTO) {
        try {
            if (imageDiagnosticCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validamos que el doctor exista
            Doctor doctor = doctorRepository.findById(imageDiagnosticCreateDTO.getDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + imageDiagnosticCreateDTO.getDoctorId()));

            // Validamos que la imagen exista
            MedicalImage medicalImg = medicalImgRepository.findByIdIncludingRetired(imageDiagnosticCreateDTO.getMedicalImgId())
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + imageDiagnosticCreateDTO.getMedicalImgId()));
            // Una imagen retirada no se vuelve a clasificar (ADR-013)
            if (medicalImg.isRetired()) {
                return ApiResponse.error("La imagen médica " + medicalImg.getId() + " está retirada");
            }

            // Solo se clasifican imágenes cuyo tipo tiene un dataset configurado
            Dataset dataset = datasetRepository.findByMedicalImageTypeId(medicalImg.getMedicalImageType().getId()).orElse(null);
            if (dataset == null) {
                return ApiResponse.error("El tipo de imagen médica no tiene un dataset configurado; no se puede clasificar");
            }

        // Validamos que ese medico si pueda diagnosticar ese tipo de imagenes medicas
            // Validamos que este tipo de imagen tenga area de evaluacion asignada.
            if( medicalImg.getMedicalImageType().getEvaluationArea() == null){
                return ApiResponse.error("El tipo de imagen médica no tiene un área de evaluación asignada, no hay personal autorizado para su clasificacion");
            }

            // Validamos si ese doctor pertenece a esa area de evaluacion
            Integer evaluationAreaRequiredId = medicalImg.getMedicalImageType().getEvaluationArea().getId(); // Id del area de evaluacion al que esta asiganado ese tipo de imagen
            if(!doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(doctor.getId(), evaluationAreaRequiredId)){
                return ApiResponse.error("El doctor no tiene autorización para diagnosticar ese tipo de imagen médica");
            }

            // La categoría hacia la que se desliza la imagen debe pertenecer al dataset de su tipo (ADR-010)
            DatasetCategory datasetCategory = datasetCategoryRepository.findById(imageDiagnosticCreateDTO.getDatasetCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + imageDiagnosticCreateDTO.getDatasetCategoryId()));
            if (!datasetCategory.getDataset().getId().equals(dataset.getId())) {
                return ApiResponse.error("La categoría no pertenece al dataset '" + dataset.getName() + "' del tipo de imagen");
            }

            if (validateSameImgDiagnostic(imageDiagnosticCreateDTO.getDoctorId(), imageDiagnosticCreateDTO.getMedicalImgId())) {
                throw new IllegalArgumentException("Ya existe un diagnóstico dado por ese doctor a esa imagen médica");
            }


            ImageClassification imageDiagnostic = imageDiagnosticMapper.toEntity(imageDiagnosticCreateDTO);
            imageDiagnostic.setDoctor(doctor);
            imageDiagnostic.setMedicalImg(medicalImg);
            imageDiagnostic.setDatasetCategory(datasetCategory);
            //agregamos la fecha actual al crear el diagnostico
            imageDiagnostic.setDiagnosticDate(LocalDateTime.now());

            return ApiResponse.success(toResponse(imageDiagnosticRepository.save(imageDiagnostic)), "Diagnóstico de imagen creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    // Transaccional: con open-in-view=false, revisar la imagen (relación perezosa) fuera de una transacción falla
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            ImageClassification imageDiagnostic = imageDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));

            // Las clasificaciones de una imagen retirada quedan congeladas (ADR-013)
            if (imageDiagnostic.getMedicalImg() != null && imageDiagnostic.getMedicalImg().isRetired()) {
                return ApiResponse.error("La imagen médica " + imageDiagnostic.getMedicalImg().getId() + " está retirada");
            }

            imageDiagnosticRepository.delete(imageDiagnostic);
            return ApiResponse.success("Diagnóstico de imagen eliminado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    // Validacion si existe un diagnostico previo de este doctor a esta imagen.
    private boolean validateSameImgDiagnostic(Integer doctorId, Integer imgId) {
        return imageDiagnosticRepository.findByDoctorIdAndMedicalImageId(doctorId, imgId).isPresent();
    }

    // Diagnósticos derivados de la categoría según su definición vigente al deslizar (ADR-010, ADR-011)
    private ImageClassificationResponseDTO toResponse(ImageClassification imageClassification) {
        ImageClassificationResponseDTO response = imageDiagnosticMapper.toResponseDTO(imageClassification);
        if (imageClassification.getDatasetCategory() != null) {
            response.setDiagnostics(categoryDiagnosticRepository
                    .findDefinitionAt(imageClassification.getDatasetCategory().getId(), imageClassification.getDiagnosticDate()).stream()
                    .map(categoryDiagnostic -> medicalDiagnosticMapper.toSummaryDTO(categoryDiagnostic.getMedicalDiagnostic()))
                    .toList());
        }
        return response;
    }
}
