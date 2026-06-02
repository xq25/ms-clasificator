package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticResponseDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
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

    /**
     * Obtener todos los diagnósticos de imagen
     * @return Lista de todos los diagnósticos de imagen
     */
    @Transactional(readOnly = true)
    public List<ImageDiagnosticResponseDTO> findAll() {
        return imageDiagnosticRepository.findAll()
                .stream()
                .map(imageDiagnosticMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApiResponse<ImageDiagnosticResponseDTO> findById(Integer id) {
        try {
            ImageDiagnostic imageDiagnostic = imageDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));
            return ApiResponse.success(imageDiagnosticMapper.toResponseDTO(imageDiagnostic), "Diagnóstico de imagen encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar diagnóstico de imagen: " + ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<ImageDiagnosticResponseDTO> create(ImageDiagnosticCreateDTO imageDiagnosticCreateDTO) {
        try {
            if (imageDiagnosticCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            Doctor doctor = doctorRepository.findById(imageDiagnosticCreateDTO.getDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + imageDiagnosticCreateDTO.getDoctorId()));

            MedicalImg medicalImg = medicalImgRepository.findById(imageDiagnosticCreateDTO.getMedicalImgId())
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + imageDiagnosticCreateDTO.getMedicalImgId()));

            MedicalImageType imageType = medicalImg.getMedicalImageType();
            DoctorArea doctorArea = doctorAreaRepository.findByDoctorIdAndEvaluationAreaId(
                    doctor.getId(), imageType.getEvaluationArea().getId());
            if (doctorArea == null) {
                throw new IllegalArgumentException("El doctor no pertenece al área de evaluación correspondiente al tipo de imagen");
            }

            if (validateSameImgDiagnostic(imageDiagnosticCreateDTO.getDoctorId(), imageDiagnosticCreateDTO.getMedicalImgId())) {
                throw new IllegalArgumentException("Ya existe un diagnóstico dado por ese doctor a esa imagen médica");
            }

            ImageDiagnostic imageDiagnostic = ImageDiagnostic.builder()
                    .doctor(doctor)
                    .medicalImg(medicalImg)
                    .diagnosticDate(LocalDateTime.now())
                    .build();

            ImageDiagnostic saved = imageDiagnosticRepository.save(imageDiagnostic);
            return ApiResponse.success(imageDiagnosticMapper.toResponseDTO(saved), "Diagnóstico de imagen creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al crear diagnóstico de imagen: " + ex.getMessage());
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
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar diagnóstico de imagen: " + ex.getMessage());
        }
    }

    private boolean validateSameImgDiagnostic(Integer doctorId, Integer imgId) {
        return imageDiagnosticRepository.findByDoctor_IdAndMedicalImg_Id(doctorId, imgId) != null;
    }
}
