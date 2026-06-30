package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticResponseDTO;
import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticSummaryDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import org.springframework.data.domain.Page;
import Backend.ms_clasificator.Mappers.ImageDiagnosticMappers.ImageDiagnosticMapper;
import Backend.ms_clasificator.Models.*;
import Backend.ms_clasificator.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImageDiagnosticService {

    @Autowired
    private ImageDiagnosticRepository imageDiagnosticRepository;

    @Autowired
    private ImageDiagnosticMapper imageDiagnosticMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private MedicalImgRepository medicalImgRepository;

    @Autowired
    private DoctorAreaRepository doctorAreaRepository;

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ImageDiagnosticSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        Page<ImageDiagnosticSummaryDTO> page = imageDiagnosticRepository.findAll(pageRequest.toPageable())
                .map(imageDiagnosticMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<ImageDiagnosticSummaryDTO>builder()
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
    public ApiResponse<ImageDiagnosticResponseDTO> findById(Integer id) {
        try {
            ImageDiagnosticResponseDTO imageDiagnostic = imageDiagnosticRepository.findById(id)
                    .map(imageDiagnosticMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));

            return ApiResponse.success(imageDiagnostic, "Diagnóstico de imagen encontrado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ImageDiagnosticSummaryDTO>> findByDoctorId(Integer doctorId, PageRequestDTO pageRequest) {
        try {
            Page<ImageDiagnosticSummaryDTO> page = imageDiagnosticRepository
                    .findByDoctorId(doctorId, pageRequest.toPageable())
                    .map(imageDiagnosticMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<ImageDiagnosticSummaryDTO>builder()
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
    public ApiResponse<PagedResponse<ImageDiagnosticSummaryDTO>> findByMedicalImgId(Integer medicalImgId, PageRequestDTO pageRequest) {
        try {
            Page<ImageDiagnosticSummaryDTO> page = imageDiagnosticRepository
                    .findByMedicalImgId(medicalImgId, pageRequest.toPageable())
                    .map(imageDiagnosticMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<ImageDiagnosticSummaryDTO>builder()
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
    public ApiResponse<ImageDiagnosticResponseDTO> create(ImageDiagnosticCreateDTO imageDiagnosticCreateDTO) {
        try {
            if (imageDiagnosticCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validamos que el doctor exista
            Doctor doctor = doctorRepository.findById(imageDiagnosticCreateDTO.getDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + imageDiagnosticCreateDTO.getDoctorId()));

            // Validamos que la imagen exista
            MedicalImg medicalImg = medicalImgRepository.findById(imageDiagnosticCreateDTO.getMedicalImgId())
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


            ImageDiagnostic imageDiagnostic = imageDiagnosticMapper.toEntity(imageDiagnosticCreateDTO);
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
            ImageDiagnostic imageDiagnostic = imageDiagnosticRepository.findById(id)
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
