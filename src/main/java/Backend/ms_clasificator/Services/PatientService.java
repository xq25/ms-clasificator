package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Patient.PatientCreateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.PatientMappers.PatientMapper;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Models.Patient;
import Backend.ms_clasificator.Repositories.MedicalImgRepository;
import Backend.ms_clasificator.Repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {


    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private MedicalImgRepository medicalImgRepository;

    @Autowired SecurityServices securityServices;

    /**
     * Obtener todos los pacientes
     * @return Lista de todos los pacientes
     */
    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    /**
     * Obtener un paciente por ID
     * @param id ID del paciente
     * @return Paciente encontrado o null
     */
    @Transactional(readOnly = true)
    public ApiResponse<Patient> findById(Integer id) {
        try{
            Patient patient = patientRepository.findById(id).
                    orElseThrow(() -> new IllegalArgumentException(
                            "Paciente no encontrado con ID: " + id));

            return ApiResponse.success(patient, "Paciente encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar paciente: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Patient> findByDocument(String document) {
        try {
            Patient patient = patientRepository.findByDocument(document);
            if (patient == null) {
                return ApiResponse.error("No se encontro paciente con el documento: " + document);
            }
            return ApiResponse.success(patient, "Pacientes encontrados exitosamente");
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar pacientes por documento: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Patient> findByUserId(String userId) {
        try {
            Patient patient = patientRepository.findByUserId(userId);
            if (patient == null) {
                return ApiResponse.error("No se encontro paciente con el userId: " + userId);
            }
            return ApiResponse.success(patient, "Paciente encontrado exitosamente");
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar paciente por userId: " + ex.getMessage());
        }
    }

    /**
     * Crear un nuevo paciente
     * @param patientCreateDTO DTO con datos de entrada
     * @return ApiResponse<Patient> con el resultado de la operación
     */
    public ApiResponse<Patient> create(PatientCreateDTO patientCreateDTO) {
        try {
            if (patientCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que no exista paciente con el mismo documento
            if (patientRepository.findByDocument(patientCreateDTO.getDocument()) != null) {
                return ApiResponse.error("Ya existe un paciente con el documento: " + patientCreateDTO.getDocument());
            }

            // Validar que exista este user_id en ms-security
            boolean exist = this.securityServices.existUserById(patientCreateDTO.getUserId());

            if(!exist){
                return ApiResponse.error("No existe un usuario con id" + patientCreateDTO.getUserId());
            }

            // Validar que no exista paciente con el mismo userId
            if (patientRepository.findByUserId(patientCreateDTO.getUserId()) != null) {
                return ApiResponse.error("Ya existe un paciente con el userId: " + patientCreateDTO.getUserId() + ". El userId debe ser único.");
            }

            Patient patient = patientMapper.toEntity(patientCreateDTO);
            Patient saved = patientRepository.save(patient);
            return ApiResponse.success(saved, "Paciente creado exitosamente");

        } catch (Exception ex) {
            return ApiResponse.error("Error al crear paciente: " + ex.getMessage());
        }
    }

    /**
     * Actualizar un paciente existente
     * @param id ID del paciente a actualizar
     * @param patientCreateDTO DTO con datos a actualizar
     * @return ApiResponse<Patient> con el resultado de la operación
     */
    public ApiResponse<Patient> update(Integer id, PatientCreateDTO patientCreateDTO) {
        try {
            if (patientCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

            // Validar que no exista otro paciente con el mismo documento
            Patient existingByDocument = patientRepository.findByDocument(patientCreateDTO.getDocument());
            if (existingByDocument != null && !existingByDocument.getId().equals(id)) {
                return ApiResponse.error("Ya existe un paciente con el documento: " + patientCreateDTO.getDocument());
            }

            // Validar que no exista otro paciente con el mismo userId
            Patient existingByUserId = patientRepository.findByUserId(patientCreateDTO.getUserId());
            if (existingByUserId != null && !existingByUserId.getId().equals(id)) {
                return ApiResponse.error("Ya existe un paciente con el userId: " + patientCreateDTO.getUserId() + ". El userId debe ser único.");
            }

            patient.setDocument(patientCreateDTO.getDocument());
            patient.setYears(patientCreateDTO.getYears());
            patient.setUserId(patientCreateDTO.getUserId());

            Patient updated = patientRepository.save(patient);
            return ApiResponse.success(updated, "Paciente actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar paciente: " + ex.getMessage());
        }
    }

    /**
     * Eliminar un paciente por ID
     * @param id ID del paciente a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse<Void> delete(Integer id) {
        try {
            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

            // Validamos que el paciente no tenga imagenes asociadas
            List<MedicalImg> medicalImgs = this.medicalImgRepository.findByPatientId(id);
            if (!medicalImgs.isEmpty()) {
                return ApiResponse.error("No se puede eliminar el paciente porque tiene imagenes medicas asociadas");
            }


            patientRepository.delete(patient);
            return ApiResponse.success("Paciente eliminado exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violacion a integridad de la base de datos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar paciente: " + ex.getMessage());
        }
    }


}
