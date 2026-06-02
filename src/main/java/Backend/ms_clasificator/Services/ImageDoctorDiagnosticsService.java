package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsResponseDTO;
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
    public ApiResponse<List<ImageDoctorDiagnosticsResponseDTO>> findAll() {

        try {

            List<ImageDoctorDiagnosticsResponseDTO> diagnostics =
                    repository.findAll()
                            .stream()
                            .map(mapper::toResponseDTO)
                            .toList();

            return ApiResponse.success(
                    diagnostics,
                    "Diagnósticos de imágenes obtenidos exitosamente");

        } catch (Exception ex) {

            return ApiResponse.error(
                    "Error al listar diagnósticos de imágenes: "
                            + ex.getMessage());
        }
    }

    /**
     * Buscar por ID.
     */
    @Transactional(readOnly = true)
    public ApiResponse<ImageDoctorDiagnosticsResponseDTO> findById(
            Integer id) {

        try {

            ImageDoctorDiagnostics diagnostic =
                    repository.findById(id)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Diagnóstico de imagen no encontrado con ID: " + id));

            return ApiResponse.success(
                    mapper.toResponseDTO(diagnostic),
                    "Diagnóstico encontrado");

        } catch (IllegalArgumentException ex) {

            return ApiResponse.error(ex.getMessage());

        } catch (Exception ex) {

            return ApiResponse.error(
                    "Error al buscar diagnóstico: "
                            + ex.getMessage());
        }
    }

    /**
     * Obtener diagnósticos asociados a una imagen.
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<ImageDoctorDiagnosticsResponseDTO>> findByImageDiagnosticId(Integer imageDiagnosticId) {

        try {

            ImageDiagnostic imageDiagnostic =
                    imageDiagnosticRepository.findById(imageDiagnosticId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "ImageDiagnostic no encontrado con ID: "
                                                    + imageDiagnosticId));

            List<ImageDoctorDiagnosticsResponseDTO> diagnostics =
                    repository.findByImageDiagnosticId(
                                    imageDiagnostic.getId())
                            .stream()
                            .map(mapper::toResponseDTO)
                            .toList();

            return ApiResponse.success(
                    diagnostics,
                    "Diagnósticos obtenidos exitosamente");

        } catch (IllegalArgumentException ex) {

            return ApiResponse.error(ex.getMessage());

        } catch (Exception ex) {

            return ApiResponse.error(
                    "Error al buscar diagnósticos: "
                            + ex.getMessage());
        }
    }

    /**
     * Crear asociación imagen-diagnóstico.
     */
    @Transactional
    public ApiResponse<ImageDoctorDiagnosticsResponseDTO> create(
            ImageDoctorDiagnosticsCreateDTO dto) {

        try {

            if (dto == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            ImageDiagnostic imageDiagnostic =
                    imageDiagnosticRepository.findById(
                                    dto.getImageDiagnosticId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "ImageDiagnostic no encontrado con ID: "
                                                    + dto.getImageDiagnosticId()));

            MedicalDiagnostic medicalDiagnostic =
                    medicalDiagnosticRepository.findById(
                                    dto.getMedicalDiagnosticId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "MedicalDiagnostic no encontrado con ID: "
                                                    + dto.getMedicalDiagnosticId()));

            // Validar que no se le asigne un diagnostico repetido a la imagen
            boolean exists =
                    repository.existsByImageDiagnostic_IdAndMedicalDiagnosticId(imageDiagnostic.getId(), medicalDiagnostic.getId());

            if (exists) {

                return ApiResponse.error(
                        "Este diagnóstico ya fue asignado a la imagen");
            }

            ImageDoctorDiagnostics entity =
                    mapper.toEntity(dto);

            entity.setImageDiagnostic(imageDiagnostic);
            entity.setMedicalDiagnostic(medicalDiagnostic);

            ImageDoctorDiagnostics saved =
                    repository.save(entity);

            return ApiResponse.success(
                    mapper.toResponseDTO(saved),
                    "Diagnóstico asignado exitosamente");

        } catch (IllegalArgumentException ex) {

            return ApiResponse.error(ex.getMessage());

        } catch (Exception ex) {

            return ApiResponse.error(
                    "Error al crear diagnóstico de imagen: "
                            + ex.getMessage());
        }
    }


    /**
     * Eliminar asociación.
     */
    @Transactional
    public ApiResponse<Void> delete(Integer id) {

        try {

            ImageDoctorDiagnostics entity =
                    repository.findById(id)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Diagnóstico de imagen no encontrado con ID: "
                                                    + id));

            repository.delete(entity);

            return ApiResponse.success(
                    "Diagnóstico eliminado exitosamente");

        } catch (IllegalArgumentException ex) {

            return ApiResponse.error(ex.getMessage());

        } catch (Exception ex) {

            return ApiResponse.error(
                    "Error al eliminar diagnóstico: "
                            + ex.getMessage());
        }
    }
}
