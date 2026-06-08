package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsResponseDTO;
import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsSummaryDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
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

    /**
     * Obtener todos los diagnósticos médicos asignados a imágenes.
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<ImageDoctorDiagnosticsSummaryDTO>> findAll() {

        List<ImageDoctorDiagnosticsSummaryDTO> diagnostics =
                repository.findAll()
                        .stream()
                        .map(mapper::toSummaryDTO)
                        .toList();

        return ApiResponse.success(diagnostics, "Diagnósticos de imágenes obtenidos exitosamente");
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

    /**
     * Obtener diagnósticos asociados al diagnostico de una imagen
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<ImageDoctorDiagnosticsSummaryDTO>> findByImageDiagnosticId(Integer imageDiagnosticId) {

        try {
            if(!imageDiagnosticRepository.existsById(imageDiagnosticId)){
                return ApiResponse.error("ImageDiagnostic no encontrado con ID: " + imageDiagnosticId);
            }
            List<ImageDoctorDiagnosticsSummaryDTO> diagnostics = repository.findByImageDiagnosticId(imageDiagnosticId)
                            .stream()
                            .map(mapper::toSummaryDTO)
                            .toList();

            return ApiResponse.success(diagnostics, "Diagnósticos obtenidos exitosamente");

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
