package backend.msclasificator.classification.application;

import backend.msclasificator.classification.api.dto.ImageClassificationCreateDTO;
import backend.msclasificator.classification.api.dto.ImageClassificationResponseDTO;
import backend.msclasificator.classification.api.dto.ImageClassificationSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import org.springframework.data.domain.Page;
import backend.msclasificator.classification.api.mapper.ImageClassificationMapper;
import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.clinicalrecord.domain.Diagnosis;
import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.doctor.domain.DoctorArea;
import backend.msclasificator.doctor.domain.EvaluationArea;
import backend.msclasificator.classification.domain.ImageClassification;
import backend.msclasificator.classification.domain.ClassificationDiagnosis;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.medicalimage.domain.MedicalImageType;
import backend.msclasificator.medicalimage.domain.MedicalImage;
import backend.msclasificator.patient.domain.Patient;
import backend.msclasificator.clinicalrecord.domain.PatientDatum;
import backend.msclasificator.patient.domain.PatientSex;
import backend.msclasificator.clinicalrecord.domain.PrimitiveDatum;
import backend.msclasificator.clinicalrecord.domain.PrimitiveType;
import backend.msclasificator.clinicalrecord.domain.PrimitiveUnit;
import backend.msclasificator.shared.domain.SystemDatum;
import backend.msclasificator.identity.domain.UserInfo;
import backend.msclasificator.clinicalrecord.infrastructure.persistence.ClinicalRecordRepository;
import backend.msclasificator.dataset.infrastructure.persistence.DatasetCategoryRepository;
import backend.msclasificator.dataset.infrastructure.persistence.DatasetRepository;
import backend.msclasificator.clinicalrecord.infrastructure.persistence.DiagnosisRepository;
import backend.msclasificator.dataset.infrastructure.persistence.CategoryDiagnosticRepository;
import backend.msclasificator.doctor.infrastructure.persistence.DoctorAreaRepository;
import backend.msclasificator.doctor.infrastructure.persistence.DoctorRepository;
import backend.msclasificator.doctor.infrastructure.persistence.EvaluationAreaRepository;
import backend.msclasificator.classification.infrastructure.persistence.ImageClassificationRepository;
import backend.msclasificator.classification.infrastructure.persistence.ClassificationDiagnosisRepository;
import backend.msclasificator.diagnosiscatalog.infrastructure.persistence.MedicalDiagnosticRepository;
import backend.msclasificator.medicalimage.infrastructure.persistence.MedicalImageTypeRepository;
import backend.msclasificator.medicalimage.infrastructure.persistence.MedicalImageRepository;
import backend.msclasificator.clinicalrecord.infrastructure.persistence.PatientDatumRepository;
import backend.msclasificator.patient.infrastructure.persistence.PatientRepository;
import backend.msclasificator.clinicalrecord.infrastructure.persistence.PrimitiveDatumRepository;
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

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ImageClassificationSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        Page<ImageClassificationSummaryDTO> page = imageDiagnosticRepository.findAll(pageRequest.toPageable())
                .map(imageDiagnosticMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<ImageClassificationSummaryDTO>builder()
                        .content(page.getContent())
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .last(page.isLast())
                        .build(),
                "Diagnósticos de imagen obtenidos exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(imageDiagnosticRepository.countAll(), "Total de diagnósticos de imagen");
    }

    @Transactional(readOnly = true)
    public ApiResponse<ImageClassificationResponseDTO> findById(Integer id) {
        try {
            ImageClassificationResponseDTO imageDiagnostic = imageDiagnosticRepository.findById(id)
                    .map(imageDiagnosticMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));

            return ApiResponse.success(imageDiagnostic, "Diagnóstico de imagen encontrado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ImageClassificationSummaryDTO>> findByDoctorId(Integer doctorId, PageRequestDTO pageRequest) {
        try {
            Page<ImageClassificationSummaryDTO> page = imageDiagnosticRepository
                    .findByDoctorId(doctorId, pageRequest.toPageable())
                    .map(imageDiagnosticMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<ImageClassificationSummaryDTO>builder()
                            .content(page.getContent())
                            .page(page.getNumber())
                            .size(page.getSize())
                            .totalElements(page.getTotalElements())
                            .totalPages(page.getTotalPages())
                            .last(page.isLast())
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
            Page<ImageClassificationSummaryDTO> page = imageDiagnosticRepository
                    .findByMedicalImgId(medicalImgId, pageRequest.toPageable())
                    .map(imageDiagnosticMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<ImageClassificationSummaryDTO>builder()
                            .content(page.getContent())
                            .page(page.getNumber())
                            .size(page.getSize())
                            .totalElements(page.getTotalElements())
                            .totalPages(page.getTotalPages())
                            .last(page.isLast())
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
            MedicalImage medicalImg = medicalImgRepository.findById(imageDiagnosticCreateDTO.getMedicalImgId())
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + imageDiagnosticCreateDTO.getMedicalImgId()));

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

            if (validateSameImgDiagnostic(imageDiagnosticCreateDTO.getDoctorId(), imageDiagnosticCreateDTO.getMedicalImgId())) {
                throw new IllegalArgumentException("Ya existe un diagnóstico dado por ese doctor a esa imagen médica");
            }


            ImageClassification imageDiagnostic = imageDiagnosticMapper.toEntity(imageDiagnosticCreateDTO);
            imageDiagnostic.setDoctor(doctor);
            imageDiagnostic.setMedicalImg(medicalImg);
            //agregamos la fecha actual al crear el diagnostico
            imageDiagnostic.setDiagnosticDate(LocalDateTime.now());

            return ApiResponse.success(imageDiagnosticMapper.toResponseDTO(imageDiagnosticRepository.save(imageDiagnostic)), "Diagnóstico de imagen creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    public ApiResponse<Void> delete(Integer id) {
        try {
            ImageClassification imageDiagnostic = imageDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));

            imageDiagnosticRepository.delete(imageDiagnostic);
            return ApiResponse.success("Diagnóstico de imagen eliminado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    // Validacion si existe un diagnostico previo de este doctor a esta imagen.
    private boolean validateSameImgDiagnostic(Integer doctorId, Integer imgId) {
        return imageDiagnosticRepository.findByDoctorIdAndMedicalImgId(doctorId, imgId) != null;
    }
}
