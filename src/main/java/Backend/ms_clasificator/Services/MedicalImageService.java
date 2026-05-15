package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.MedicalImgMappers.MedicalImgMapper;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Models.Patient;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import Backend.ms_clasificator.Repositories.MedicalImgRepository;
import Backend.ms_clasificator.Repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    public ApiResponse<MedicalImg> findById(Integer id) {
        try{
            MedicalImg medicalImage = this.medicalImgRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + id));
            return ApiResponse.success(medicalImage, "Imagen médica obtenida exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al obtener imagen médica: " + ex.getMessage());
        }
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
                throw new IllegalArgumentException("El ID del área de evaluación no puede ser nulo");
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

    @Transactional(readOnly = true)
    public ApiResponse<List<MedicalImg>> findByPatientId(Integer patientId) {
        try {
            if (patientId == null) {
                return ApiResponse.error("El ID del paciente no puede ser nulo");
            }

            // Validar que exista el paciente
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + patientId));

            List<MedicalImg> images = medicalImgRepository.findByPatientId(patientId);

            if (images.isEmpty()) {
                return ApiResponse.success(images, "No hay imágenes médicas asignadas a este paciente");
            }

            return ApiResponse.success(images, "Imágenes médicas del paciente obtenidas exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al obtener imágenes médicas del paciente: " + ex.getMessage());
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
     * @param medicalImgUpdateDTO DTO con datos a actualizar
     * @return ApiResponse<MedicalImg> con el resultado de la operación
     */
    public ApiResponse<MedicalImg> update(Integer id, MedicalImgUpdateDTO medicalImgUpdateDTO) {
        try {
            if (medicalImgUpdateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            MedicalImg medicalImg = medicalImgRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + id));

            // Asiganamos la URL nueva
            medicalImg.setUrl(medicalImgUpdateDTO.getUrl());

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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse<Void> delete(Integer id) {
        try {
            MedicalImg medicalImg = medicalImgRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen médica no encontrada con ID: " + id));

            medicalImgRepository.delete(medicalImg);
            return ApiResponse.success("Imagen médica eliminada exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("No se puede eliminar la Imagen Medica porque tiene diagnosticos asiganados por doctores");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar imagen médica: " + ex.getMessage());
        }
    }

    // ---------- Generacion y Eliminacion de relaciones -------------
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

            //Validamos que no tenga un paciente ya asiganado.
            if (medicalImg.getPatientId() != null){
                throw new IllegalArgumentException("La imagen médica ya tiene un paciente asignado. Remueva el paciente actual antes de asignar uno nuevo.");
            }

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
            if (medicalImg.getPatientId() == null){
                throw new IllegalArgumentException("La imagen médica no tiene un paciente asignado.");
            }

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
