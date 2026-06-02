package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticCreateDTO;
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
    public List<ImageDiagnostic> findAll() {
        return imageDiagnosticRepository.findAll();
    }

    /**
     * Obtener un diagnóstico de imagen por ID
     * @param id ID del diagnóstico de imagen
     * @return Diagnóstico encontrado o null
     */
    @Transactional(readOnly = true)
    public ApiResponse<ImageDiagnostic> findById(Integer id) {
        try{
            ImageDiagnostic imageDiagnostic = imageDiagnosticRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Diagnóstico de imagen no encontrado con ID: " + id));
            return ApiResponse.success(imageDiagnostic, "Diagnóstico de imagen encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar diagnóstico de imagen: " + ex.getMessage());
        }
    }

    /**
     * Crear un nuevo diagnóstico de imagen
     * @param imageDiagnosticCreateDTO DTO con datos de entrada
     * @return ApiResponse<ImageDiagnostic> con el resultado de la operación
     */
    @Transactional
    public ApiResponse<ImageDiagnostic> create(ImageDiagnosticCreateDTO imageDiagnosticCreateDTO) {
        try {
            if (imageDiagnosticCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }
        // Validaciones de coherencia de datos.

            // Validar que exista el doctor
            Doctor doctor = doctorRepository.findById(imageDiagnosticCreateDTO.getDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + imageDiagnosticCreateDTO.getDoctorId()));

            // Validar que exista la imagen médica
            MedicalImg medicalImg = medicalImgRepository.findById(imageDiagnosticCreateDTO.getMedicalImgId())
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + imageDiagnosticCreateDTO.getMedicalImgId()));


        // Validar que el doctor pertenece al mismo area de evalaucion que el tipo de imagen que esta por clasificar
            MedicalImageType imageType = medicalImg.getMedicalImageType();
            DoctorArea doctorArea = this.doctorAreaRepository.findByDoctorIdAndEvaluationAreaId(doctor.getId(), imageType.getEvaluationArea().getId());
            if (doctorArea == null) {
                throw new IllegalArgumentException("El doctor no pertenece al área de evaluación correspondiente al tipo de imagen");
            }

        // Validacion de correctitud de logica y preservacion de datos.

            if (this.validateSameImgDiagnostic(imageDiagnosticCreateDTO.getDoctorId(), imageDiagnosticCreateDTO.getMedicalImgId())){
                throw new IllegalArgumentException("Ya existe un diagnóstico dado por ese doctor a esa imagen médica");
            }


        // Asignar la fecha actual al diagnostico
            LocalDateTime diagnosticDate = LocalDateTime.now();

            ImageDiagnostic imageDiagnostic = ImageDiagnostic.builder()
                    .doctor(doctor)
                    .medicalImg(medicalImg)
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

    private boolean validateSameImgDiagnostic(Integer doctor_id, Integer img_id){
        boolean validation = false;
        ImageDiagnostic diagnostic = imageDiagnosticRepository.findByDoctor_IdAndMedicalImg_Id(doctor_id, img_id);
        // Si existe un registro ya generado por ese medico a esa imagen, devolvemos true.
        if (diagnostic != null){
            validation = true;
        }
        return validation;
    }
}
