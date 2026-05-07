package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.ImageDiagnosticMappers.ImageDiagnosticMapper;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.ImageDiagnostic;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Repositories.DoctorRepository;
import Backend.ms_clasificator.Repositories.ImageDiagnosticRepository;
import Backend.ms_clasificator.Repositories.MedicalDiagnosticRepository;
import Backend.ms_clasificator.Repositories.MedicalImgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    /**
     * Obtener todos los diagnósticos de imagen
     * @return Lista de todos los diagnósticos de imagen
     */
    public List<ImageDiagnostic> findAll() {
        return imageDiagnosticRepository.findAll();
    }

    /**
     * Obtener un diagnóstico de imagen por ID
     * @param id ID del diagnóstico de imagen
     * @return Diagnóstico encontrado o null
     */
    public ImageDiagnostic findById(Integer id) {
        return imageDiagnosticRepository.findById(id).orElse(null);
    }

    /**
     * Crear un nuevo diagnóstico de imagen
     * @param imageDiagnosticCreateDTO DTO con datos de entrada
     * @return ApiResponse<ImageDiagnostic> con el resultado de la operación
     */
    public ApiResponse<ImageDiagnostic> create(ImageDiagnosticCreateDTO imageDiagnosticCreateDTO) {
        try {
            if (imageDiagnosticCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que exista el doctor
            Doctor doctor = doctorRepository.findById(imageDiagnosticCreateDTO.getDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + imageDiagnosticCreateDTO.getDoctorId()));

            // Validar que exista la imagen médica
            MedicalImg medicalImg = medicalImgRepository.findById(imageDiagnosticCreateDTO.getMedicalImgId())
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + imageDiagnosticCreateDTO.getMedicalImgId()));

            // Validar que exista el diagnóstico médico
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(imageDiagnosticCreateDTO.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + imageDiagnosticCreateDTO.getMedicalDiagnosticId()));

            // Asignar la fecha actual si no se proporciona
            LocalDateTime diagnosticDate = imageDiagnosticCreateDTO.getDiagnosticDate() != null 
                    ? imageDiagnosticCreateDTO.getDiagnosticDate()
                    : LocalDateTime.now();

            ImageDiagnostic imageDiagnostic = ImageDiagnostic.builder()
                    .doctor(doctor)
                    .medicalImg(medicalImg)
                    .medicalDiagnostic(medicalDiagnostic)
                    .diagnosticDate(diagnosticDate)
                    .build();

            ImageDiagnostic saved = imageDiagnosticRepository.save(imageDiagnostic);
            return ApiResponse.success(saved, "Diagnóstico de imagen creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al crear diagnóstico de imagen: " + ex.getMessage());
        }
    }

    /**
     * Actualizar un diagnóstico de imagen existente
     * @param id ID del diagnóstico a actualizar
     * @param imageDiagnosticCreateDTO DTO con datos a actualizar
     * @return ApiResponse<ImageDiagnostic> con el resultado de la operación
     */
    public ApiResponse<ImageDiagnostic> update(Integer id, ImageDiagnosticCreateDTO imageDiagnosticCreateDTO) {
        try {
            if (imageDiagnosticCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            ImageDiagnostic imageDiagnostic = imageDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));

            // Validar que exista el doctor
            Doctor doctor = doctorRepository.findById(imageDiagnosticCreateDTO.getDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + imageDiagnosticCreateDTO.getDoctorId()));

            // Validar que exista la imagen médica
            MedicalImg medicalImg = medicalImgRepository.findById(imageDiagnosticCreateDTO.getMedicalImgId())
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + imageDiagnosticCreateDTO.getMedicalImgId()));

            // Validar que exista el diagnóstico médico
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(imageDiagnosticCreateDTO.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + imageDiagnosticCreateDTO.getMedicalDiagnosticId()));

            imageDiagnostic.setDoctor(doctor);
            imageDiagnostic.setMedicalImg(medicalImg);
            imageDiagnostic.setMedicalDiagnostic(medicalDiagnostic);
            if (imageDiagnosticCreateDTO.getDiagnosticDate() != null) {
                imageDiagnostic.setDiagnosticDate(imageDiagnosticCreateDTO.getDiagnosticDate());
            }

            ImageDiagnostic updated = imageDiagnosticRepository.save(imageDiagnostic);
            return ApiResponse.success(updated, "Diagnóstico de imagen actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar diagnóstico de imagen: " + ex.getMessage());
        }
    }

    /**
     * Eliminar un diagnóstico de imagen por ID
     * @param id ID del diagnóstico a eliminar
     * @return ApiResponse con el resultado de la operación
     */
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
}
