package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Patient.PatientCreateDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientResponseDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientSummaryDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.PatientMappers.PatientMapper;
import Backend.ms_clasificator.Models.Patient;
import Backend.ms_clasificator.Models.UserInfo;
import Backend.ms_clasificator.Repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {


    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired SecurityServices securityServices;

    /**
     * Obtener todos los pacientes
     * @return Lista de todos los pacientes
     */
    @Transactional(readOnly = true)
    public List<PatientSummaryDTO> findAll() {
        return patientRepository.findAll().stream().map(patientMapper::toSummaryDTO).toList();
    }

    /**
     * Obtener un paciente por ID
     * @param id ID del paciente
     * @return Paciente encontrado o null
     */
    @Transactional(readOnly = true)
    public ApiResponse<PatientResponseDTO> findById(Integer id) {
        try{
            // se genera el dto con los datos del user vacios y se le agregan despues.
            PatientResponseDTO patient = patientRepository.findById(id).map(patientMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

            // Obtenemos la informacion del usuario
            UserInfo userInfo = securityServices.getUserInfo(patient.getUserId());
            if(userInfo != null){
                patient.setUserInfo(userInfo);
            } else {
                ApiResponse.error("No se pudo obtener la informacion del usuario para el userId: " + patient.getUserId());
            }

            return ApiResponse.success(patient, "Paciente encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar paciente: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PatientResponseDTO> findByDocument(String document) {
        try {
            PatientResponseDTO patient = patientRepository.findByDocument(document)
                    .map(patientMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontro paciente con el documento: " + document));

            // Obtenemos la informacion del usuario
            UserInfo userInfo = securityServices.getUserInfo(patient.getUserId());
            if(userInfo != null){
                patient.setUserInfo(userInfo);
            } else {
                ApiResponse.error("No se pudo obtener la informacion del usuario para el userId: " + patient.getUserId());
            }

            return ApiResponse.success(patient, "Pacientes encontrados exitosamente");
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar pacientes por documento: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PatientResponseDTO> findByUserId(String userId) {
        try {
            PatientResponseDTO patient = patientRepository.findByUserId(userId)
                    .map(patientMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontro paciente con el userId: " + userId));

            // Obtenemos la informacion del usuario
            UserInfo userInfo = securityServices.getUserInfo(patient.getUserId());
            if(userInfo != null){
                patient.setUserInfo(userInfo);
            } else {
                ApiResponse.error("No se pudo obtener la informacion del usuario para el userId: " + patient.getUserId());
            }

            return ApiResponse.success(patient, "Paciente encontrado exitosamente");
        } catch (IllegalArgumentException ex){
            return ApiResponse.error(ex.getMessage());

        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar paciente por userId: " + ex.getMessage());
        }
    }

    /**
     * Crear un nuevo paciente
     * @param patientCreateDTO DTO con datos de entrada
     * @return ApiResponse<Patient> con el resultado de la operación
     */
    public ApiResponse<PatientSummaryDTO> create(PatientCreateDTO patientCreateDTO) {
        try {
            if (patientCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que no exista paciente con el mismo documento
            if (patientRepository.findByDocument(patientCreateDTO.getDocument()).orElse(null) != null) {
                return ApiResponse.error("Ya existe un paciente con el documento: " + patientCreateDTO.getDocument());
            }

            // Validar que exista este user_id en ms-security
            boolean exist = this.securityServices.existUserById(patientCreateDTO.getUserId());
            if(!exist){
                return ApiResponse.error("No existe un usuario con id" + patientCreateDTO.getUserId());
            }

            // Validar que no exista paciente con el mismo userId
            if (patientRepository.findByUserId(patientCreateDTO.getUserId()).orElse(null) != null) {
                return ApiResponse.error("Ya existe un paciente con el userId: " + patientCreateDTO.getUserId() + ". El userId debe ser único.");
            }

            // Asignamos el rol por defecto a este paciente
            boolean assingRole = this.securityServices.assignDefaultRole(patientCreateDTO.getUserId(), "patient");
            String roleInfo = assingRole?"Rol 'patient' asignado correctamente al usuario.": "No se pudo asignar el rol 'doctor' al usuario.";

            Patient saved = patientRepository.save(patientMapper.toEntity(patientCreateDTO));

            return ApiResponse.success(patientMapper.toSummaryDTO(saved), "Paciente creado exitosamente. " + roleInfo);

        } catch (Exception ex) {
            return ApiResponse.error("Error al crear paciente: " + ex.getMessage());
        }
    }

    /**
     * Actualizar un paciente existente
     * @param id ID del paciente a actualizar
     * @param patientUpdateDTO DTO con datos a actualizar
     * @return ApiResponse<Patient> con el resultado de la operación
     */
    public ApiResponse<PatientSummaryDTO> update(Integer id, PatientUpdateDTO patientUpdateDTO) {
        try {
            if (patientUpdateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

            // Validar que no exista otro paciente con el mismo documento
            Patient existingByDocument = patientRepository.findByDocument(patientUpdateDTO.getDocument()).orElse(null);
            if (existingByDocument != null && !existingByDocument.getId().equals(id)) {
                return ApiResponse.error("Ya existe un paciente con el documento: " + patientUpdateDTO.getDocument());
            }

            patient.setDocument(patientUpdateDTO.getDocument());
            patient.setYears(patientUpdateDTO.getYears());

            Patient updated = patientRepository.save(patient);
            return ApiResponse.success(patientMapper.toSummaryDTO(updated), "Paciente actualizado exitosamente");

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
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

            // Un paciente se puede eliminar sin restricciones. Pero hay que mostrar una alerta antes de hacerlo.

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
