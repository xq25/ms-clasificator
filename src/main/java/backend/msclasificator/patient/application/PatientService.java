package backend.msclasificator.patient.application;

import backend.msclasificator.identity.application.port.out.IdentityProvider;

import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.patient.api.dto.PatientCreateDTO;
import backend.msclasificator.patient.api.dto.PatientResponseDTO;
import backend.msclasificator.patient.api.dto.PatientSummaryDTO;
import backend.msclasificator.patient.api.dto.PatientUpdateDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.patient.api.mapper.PatientMapper;
import backend.msclasificator.patient.domain.Patient;
import backend.msclasificator.identity.application.exception.SecurityServiceException;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageRepository;
import backend.msclasificator.patient.application.port.out.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class    PatientService {


    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalImageRepository medicalImgRepository;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired IdentityProvider identityProvider;

    /**
     * Obtener todos los pacientes
     * @return Lista de todos los pacientes
     */
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<PatientSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<PatientSummaryDTO> page = patientRepository.findAll(pageRequest.toPageQuery())
                .map(patientMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<PatientSummaryDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Pacientes encontrados exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<PatientSummaryDTO>> searchByDocument(String query, PageRequestDTO pageRequest) {
        try {
            PageResult<PatientSummaryDTO> page = patientRepository
                    .searchByDocument(query, pageRequest.toPageQuery())
                    .map(patientMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<PatientSummaryDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
                            .build(),
                    "Resultados de búsqueda por documento"
            );
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar pacientes por documento: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(patientRepository.count(), "Total de pacientes");
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

            // Si ms-security falla lanza SecurityServiceException, que GlobalExceptionHandler traduce a 503
            patient.setUserInfo(identityProvider.getUserInfo(patient.getUserId()));

            return ApiResponse.success(patient, "Paciente encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (SecurityServiceException ex) {
            throw ex;
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

            // Si ms-security falla lanza SecurityServiceException, que GlobalExceptionHandler traduce a 503
            patient.setUserInfo(identityProvider.getUserInfo(patient.getUserId()));

            return ApiResponse.success(patient, "Pacientes encontrados exitosamente");
        } catch (SecurityServiceException ex) {
            throw ex;
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

            // Si ms-security falla lanza SecurityServiceException, que GlobalExceptionHandler traduce a 503
            patient.setUserInfo(identityProvider.getUserInfo(patient.getUserId()));

            return ApiResponse.success(patient, "Paciente encontrado exitosamente");
        } catch (IllegalArgumentException ex){
            return ApiResponse.error(ex.getMessage());
        } catch (SecurityServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar paciente por userId: " + ex.getMessage());
        }
    }

    /**
     * Crear un nuevo paciente
     * @param patientCreateDTO DTO con datos de entrada
     * @return ApiResponse<Patient> con el resultado de la operación
     */
    // Todo o nada (bug #9): se guarda y, como último paso, se asigna el rol en ms-security.
    // Si la asignación falla, SecurityServiceException hace rollback del guardado (503, ADR-009).
    @Transactional
    public ApiResponse<PatientSummaryDTO> create(PatientCreateDTO patientCreateDTO) {
        try {
            if (patientCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que no exista paciente con el mismo documento
            if (patientRepository.findByDocument(patientCreateDTO.getDocument()).orElse(null) != null) {
                return ApiResponse.error("Ya existe un paciente con el documento: " + patientCreateDTO.getDocument());
            }

            if(patientCreateDTO.getDob() != null && patientCreateDTO.getDob().after(new Date())) {
                return ApiResponse.error("La fecha de nacimiento no puede ser futura");
            }

            // Validar que exista este user_id en ms-security
            boolean exist = this.identityProvider.existsUser(patientCreateDTO.getUserId());
            if(!exist){
                return ApiResponse.error("No existe un usuario con el userId: " + patientCreateDTO.getUserId() + ". No se puede crear el paciente sin un usuario asociado.");
            }

            // Validar que no exista paciente con el mismo userId
            if (patientRepository.findByUserId(patientCreateDTO.getUserId()).orElse(null) != null) {
                return ApiResponse.error("Ya existe un paciente con el userId: " + patientCreateDTO.getUserId() + ". El userId debe ser único.");
            }

            Patient saved = patientRepository.save(patientMapper.toEntity(patientCreateDTO));

            // Último paso: si falla, lanza SecurityServiceException y se revierte el guardado
            this.identityProvider.assignDefaultRole(patientCreateDTO.getUserId(), "patient");

            return ApiResponse.success(patientMapper.toSummaryDTO(saved), "Paciente creado exitosamente. Rol 'patient' asignado correctamente al usuario.");

        } catch (SecurityServiceException ex) {
            throw ex;
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
    @Transactional
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
            patient.setDob(patientUpdateDTO.getDob());
            patient.setSex(patientUpdateDTO.getSex());

            Patient updated = patientRepository.save(patient);
            return ApiResponse.success(patientMapper.toSummaryDTO(updated), "Paciente actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar paciente: " + ex.getMessage());
        }
    }

    /**
     * Eliminar un paciente por ID.
     * Orden de operaciones:
     *   1. Desvincular MedicalImage → clinical_record_id = NULL (UPDATE masivo por JPQL)
     *   2. Eliminar Patient → cascade + orphanRemoval borra los ClinicalRecord automáticamente
     *
     * @param id ID del paciente a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

            // 1. Desvincular en bloque todas las imágenes médicas ligadas a los
            //    ClinicalRecord de este paciente. Necesario antes del DELETE para
            //    evitar violaciones de FK (medical_img.clinical_record_id → clinical_record.id).
            medicalImgRepository.detachImagesByPatientId(id);

            // 2. Eliminar el paciente.
            //    Patient.clinicalRecords tiene cascade = ALL + orphanRemoval = true,
            //    por lo que JPA elimina los ClinicalRecord automáticamente.
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