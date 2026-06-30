package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsResponseDTO;
import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsSummaryDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import org.springframework.data.domain.Page;
import Backend.ms_clasificator.Mappers.ImageDoctorDiagnosticsMappers.ImageDoctorDiagnosticsMapper;
import Backend.ms_clasificator.Models.ImageDiagnostic;
import Backend.ms_clasificator.Models.ImageDoctorDiagnostics;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Repositories.ImageDiagnosticRepository;
import Backend.ms_clasificator.Repositories.ImageDoctorDiagnosticsRepository;
import Backend.ms_clasificator.Repositories.MedicalDiagnosticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImageDoctorDiagnosticsService {

    @Autowired
    private ImageDoctorDiagnosticsRepository repository;

    @Autowired
    private ImageDoctorDiagnosticsMapper mapper;

    @Autowired
    private ImageDiagnosticRepository imageDiagnosticRepository;

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ImageDoctorDiagnosticsSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        Page<ImageDoctorDiagnosticsSummaryDTO> page = repository.findAll(pageRequest.toPageable())
                .map(mapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<ImageDoctorDiagnosticsSummaryDTO>builder()
                        .content(page.getContent())
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .last(page.isLast())
                        .build(),
                "Diagnósticos de imágenes obtenidos exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(repository.countAll(), "Total de diagnósticos de imágenes");
    }

    /**
     * Buscar por ID.
     */
    @Transactional(readOnly = true)
    public ApiResponse<ImageDoctorDiagnosticsResponseDTO> findById(Integer id) {

        try {
            ImageDoctorDiagnosticsResponseDTO diagnostic = repository.findById(id)
                    .map(mapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));

            return ApiResponse.success(diagnostic, "Diagnóstico encontrado");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<ImageDoctorDiagnosticsSummaryDTO>> findByImageDiagnosticId(Integer imageDiagnosticId, PageRequestDTO pageRequest) {
        try {
            if (!imageDiagnosticRepository.existsById(imageDiagnosticId)) {
                return ApiResponse.error("ImageDiagnostic no encontrado con ID: " + imageDiagnosticId);
            }

            Page<ImageDoctorDiagnosticsSummaryDTO> page = repository
                    .findByImageDiagnosticId(imageDiagnosticId, pageRequest.toPageable())
                    .map(mapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<ImageDoctorDiagnosticsSummaryDTO>builder()
                            .content(page.getContent())
                            .page(page.getNumber())
                            .size(page.getSize())
                            .totalElements(page.getTotalElements())
                            .totalPages(page.getTotalPages())
                            .last(page.isLast())
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
    public ApiResponse<ImageDoctorDiagnosticsResponseDTO> create(ImageDoctorDiagnosticsCreateDTO dto) {
        try {
            if (dto == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validamos que exista el diagnostico de imagen
            ImageDiagnostic imageDiagnostic = imageDiagnosticRepository.findById(dto.getImageDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException("ImageDiagnostic no encontrado con ID: " + dto.getImageDiagnosticId()));

            // Validamos que exista el medical diagnostic
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(dto.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException("MedicalDiagnostic no encontrado con ID: " + dto.getMedicalDiagnosticId()));

            // Validar que no se le asigne un diagnostico repetido a la imagen
            if (repository.existsByImageDiagnosticIdAndMedicalDiagnosticId(imageDiagnostic.getId(), medicalDiagnostic.getId())){
                return ApiResponse.error("Este diagnóstico ya fue asignado a la imagen");
            }

            ImageDoctorDiagnostics entity = mapper.toEntity(dto);
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
            ImageDoctorDiagnostics entity = repository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));

            repository.delete(entity);
            return ApiResponse.success("Diagnóstico eliminado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }
}
