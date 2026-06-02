package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordCreateDTO;
import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordResponseDTO;
import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.ClinicalRecordMappers.ClinicalRecordMapper;
import Backend.ms_clasificator.Models.ClinicalRecord;
import Backend.ms_clasificator.Models.Patient;
import Backend.ms_clasificator.Repositories.ClinicalRecordRepository;
import Backend.ms_clasificator.Repositories.PatientRepository;
import Backend.ms_clasificator.Repositories.MedicalImgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClinicalRecordService {

    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalImgRepository medicalImgRepository;

    @Autowired
    private ClinicalRecordMapper clinicalRecordMapper;

    @Transactional(readOnly = true)
    public ApiResponse<List<ClinicalRecordResponseDTO>> findAll() {
        try {
            List<ClinicalRecordResponseDTO> response = clinicalRecordRepository.findAll()
                    .stream()
                    .map(clinicalRecordMapper::toResponseDTO)
                    .toList();
            return ApiResponse.success(response, "Registros médicos obtenidos exitosamente");
        } catch (Exception ex) {
            return ApiResponse.error("Error al listar registros médicos: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<ClinicalRecordResponseDTO> findById(Integer id) {
        try {
            ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Clinical Record no encontrado con ID: " + id));

            return ApiResponse.success(clinicalRecordMapper.toResponseDTO(clinicalRecord), "Registro médico obtenido exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al obtener registro médico: " + ex.getMessage());
        }
    }

    public ApiResponse<ClinicalRecordResponseDTO> create(ClinicalRecordCreateDTO dto) {
        try {
            if (dto == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            Patient patient = patientRepository.findById(dto.getPatientId()).orElse(null);
            if (patient == null) {
                return ApiResponse.error("El paciente asignado no existe, con id " + dto.getPatientId());
            }

            ClinicalRecord clinicalRecord = clinicalRecordMapper.toEntity(dto);
            clinicalRecord.setPatient(patient);

            ClinicalRecord saved = clinicalRecordRepository.save(clinicalRecord);
            return ApiResponse.success(clinicalRecordMapper.toResponseDTO(saved), "Registro médico creado exitosamente");

        } catch (Exception ex) {
            return ApiResponse.error("Error al crear registro médico: " + ex.getMessage());
        }
    }

    public ApiResponse<ClinicalRecordResponseDTO> update(Integer id, ClinicalRecordUpdateDTO dto) {
        try {
            if (dto == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Clinical Record no encontrado con ID: " + id));

            if (dto.getVisitDate() != null) {
                clinicalRecord.setVisitDate(dto.getVisitDate());
            }

            ClinicalRecord updated = clinicalRecordRepository.save(clinicalRecord);
            return ApiResponse.success(clinicalRecordMapper.toResponseDTO(updated), "Registro médico actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar registro médico: " + ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Clinical Record no encontrado con ID: " + id
                    ));

            // Desasociar imágenes
            if (clinicalRecord.getMedicalImages() != null && !clinicalRecord.getMedicalImages().isEmpty()) {
                clinicalRecord.getMedicalImages().forEach(img -> img.setClinicalRecord(null));
                medicalImgRepository.saveAll(clinicalRecord.getMedicalImages());
            }

            // Eliminar registro médico
            clinicalRecordRepository.delete(clinicalRecord);

            return ApiResponse.success(null, "Registro médico eliminado correctamente");
        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violacion de integridad en la base de datos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar registro médico: " + ex.getMessage());
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED) // Evitamos que si falla algo en la operacion, el clinicalRecord quede sin paciente.
    public ApiResponse<ClinicalRecordResponseDTO> changePatient(Integer clinicalRecordId, Integer newPatientId){
        try {
            // Validar que exista el clinicalRecord
            ClinicalRecord clinicalRecord = this.clinicalRecordRepository.findById(clinicalRecordId)
                    .orElseThrow(() -> new IllegalArgumentException("Clinical Record no encontrado con ID: " + clinicalRecordId));
            // Validar que exista el paciente nuevo
            Patient newPatient = this.patientRepository.findById(newPatientId).orElse(null);
            if (newPatient == null) {
                throw new IllegalArgumentException("Paciente no encontrado con ID: " + newPatientId);
            }
            // Validar que el paciente nuevo, no sea el mismo que el viejo
            if (clinicalRecord.getPatient().getId().equals(newPatientId)) {
                throw new IllegalArgumentException("El paciente nuevo no puede ser el mismo que el paciente actual");
            }
            clinicalRecord.setPatient(newPatient);
            this.clinicalRecordRepository.save(clinicalRecord);
            return ApiResponse.success(clinicalRecordMapper.toResponseDTO(clinicalRecord), "Paciente del registro médico cambiado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al cambiar paciente del registro médico: " + ex.getMessage());
        }

    }

}
