package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgCreateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.MedicalImgMappers.MedicalImgMapper;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Models.Patient;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import Backend.ms_clasificator.Repositories.MedicalImgRepository;
import Backend.ms_clasificator.Repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicalImageService {

    @Autowired
    private MedicalImgRepository medicalImgRepository;

    @Autowired
    private MedicalImgMapper medicalImgMapper;

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;

    @Autowired
    private PatientRepository patientRepository;

    /**
     * Obtener todas las imágenes médicas
     * @return Lista de todas las imágenes
     */
    @Transactional(readOnly = true)
    public List<MedicalImg> findAll() {
        return medicalImgRepository.findAll();
    }

    /**
     * Obtener una imagen médica por ID
     * @param id ID de la imagen
     * @return Imagen encontrada o null
     */
    @Transactional(readOnly = true)
    public MedicalImg findById(Integer id) {
        return medicalImgRepository.findById(id).orElse(null);
    }

    /**
     * Obtener todas las imágenes médicas de un área de evaluación
     * @param evaluationAreaId ID del área de evaluación
     * @return ApiResponse<List<MedicalImg>> con las imágenes del área
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<MedicalImg>> findByEvaluationAreaId(Integer evaluationAreaId) {
        try {
            if (evaluationAreaId == null) {
                return ApiResponse.error("El ID del área de evaluación no puede ser nulo");
            }

            // Validar que exista el área de evaluación
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + evaluationAreaId));

            List<MedicalImg> images = medicalImgRepository.findByEvaluationAreaId(evaluationAreaId);

            if (images.isEmpty()) {
                return ApiResponse.success(images, "No hay imágenes médicas en esta área de evaluación");
            }

            return ApiResponse.success(images, "Imágenes médicas obtenidas exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al obtener imágenes médicas: " + ex.getMessage());
        }
    }

    /**
     * Crear una nueva imagen médica
     * @param medicalImgCreateDTO DTO con datos de entrada
     * @return ApiResponse<MedicalImg> con el resultado de la operación
     */
    public ApiResponse<MedicalImg> create(MedicalImgCreateDTO medicalImgCreateDTO) {
        try {
            if (medicalImgCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que no esté vacío el URL
            if (medicalImgCreateDTO.getUrl() == null || medicalImgCreateDTO.getUrl().isBlank()) {
                return ApiResponse.error("La URL de la imagen no puede estar vacía");
            }

            // Validar que exista el área de evaluación (obligatorio)
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(medicalImgCreateDTO.getEvaluationAreaId())
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + medicalImgCreateDTO.getEvaluationAreaId()));

            MedicalImg medicalImg = medicalImgMapper.toEntity(medicalImgCreateDTO);
            medicalImg.setEvaluationAreaId(evaluationArea.getId());

            // Si se proporciona patientId, validar que exista y asignarlo
            if (medicalImgCreateDTO.getPatientId() != null) {
                Patient patient = patientRepository.findById(medicalImgCreateDTO.getPatientId())
                        .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + medicalImgCreateDTO.getPatientId()));
                medicalImg.setPatientId(patient.getId());
            }

            MedicalImg saved = medicalImgRepository.save(medicalImg);
            return ApiResponse.success(saved, "Imagen médica creada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al crear imagen médica: " + ex.getMessage());
        }
    }

    /**
     * Actualizar una imagen médica existente
     * @param id ID de la imagen a actualizar
     * @param medicalImgCreateDTO DTO con datos a actualizar
     * @return ApiResponse<MedicalImg> con el resultado de la operación
     */
    public ApiResponse<MedicalImg> update(Integer id, MedicalImgCreateDTO medicalImgCreateDTO) {
        try {
            if (medicalImgCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            MedicalImg medicalImg = medicalImgRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + id));

            // Validar que exista el área de evaluación
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(medicalImgCreateDTO.getEvaluationAreaId())
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + medicalImgCreateDTO.getEvaluationAreaId()));

            medicalImg.setUrl(medicalImgCreateDTO.getUrl());
            medicalImg.setEvaluationAreaId(evaluationArea.getId());

            // Actualizar paciente si se proporciona
            if (medicalImgCreateDTO.getPatientId() != null) {
                Patient patient = patientRepository.findById(medicalImgCreateDTO.getPatientId())
                        .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + medicalImgCreateDTO.getPatientId()));
                medicalImg.setPatientId(patient.getId());
            } else {
                medicalImg.setPatientId(null);
            }

            MedicalImg updated = medicalImgRepository.save(medicalImg);
            return ApiResponse.success(updated, "Imagen médica actualizada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar imagen médica: " + ex.getMessage());
        }
    }

    /**
     * Eliminar una imagen médica por ID
     * @param id ID de la imagen a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<Void> delete(Integer id) {
        try {
            MedicalImg medicalImg = medicalImgRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + id));

            medicalImgRepository.delete(medicalImg);
            return ApiResponse.success("Imagen médica eliminada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar imagen médica: " + ex.getMessage());
        }
    }

    /**
     * Asignar un paciente a una imagen médica
     * @param medicalImgId ID de la imagen
     * @param patientId ID del paciente
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<MedicalImg> assignPatient(Integer medicalImgId, Integer patientId) {
        try {
            MedicalImg medicalImg = medicalImgRepository.findById(medicalImgId)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + medicalImgId));

            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + patientId));

            medicalImg.setPatientId(patient.getId());
            MedicalImg updated = medicalImgRepository.save(medicalImg);
            return ApiResponse.success(updated, "Paciente asignado a la imagen médica exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al asignar paciente a imagen médica: " + ex.getMessage());
        }
    }

    /**
     * Remover un paciente de una imagen médica
     * @param medicalImgId ID de la imagen
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<MedicalImg> removePatient(Integer medicalImgId) {
        try {
            MedicalImg medicalImg = medicalImgRepository.findById(medicalImgId)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + medicalImgId));

            medicalImg.setPatientId(null);
            MedicalImg updated = medicalImgRepository.save(medicalImg);
            return ApiResponse.success(updated, "Paciente removido de la imagen médica exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al remover paciente de imagen médica: " + ex.getMessage());
        }
    }

    /**
     * Cambiar el área de evaluación de una imagen médica
     * @param medicalImgId ID de la imagen
     * @param evaluationAreaId ID del área de evaluación
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<MedicalImg> changeEvaluationArea(Integer medicalImgId, Integer evaluationAreaId) {
        try {
            if (evaluationAreaId == null) {
                return ApiResponse.error("El ID del área de evaluación no puede ser nulo");
            }

            MedicalImg medicalImg = medicalImgRepository.findById(medicalImgId)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + medicalImgId));

            EvaluationArea evaluationArea = evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + evaluationAreaId));

            medicalImg.setEvaluationAreaId(evaluationArea.getId());
            MedicalImg updated = medicalImgRepository.save(medicalImg);
            return ApiResponse.success(updated, "Área de evaluación asignada a la imagen médica exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al cambiar área de evaluación de imagen médica: " + ex.getMessage());
        }
    }
}
