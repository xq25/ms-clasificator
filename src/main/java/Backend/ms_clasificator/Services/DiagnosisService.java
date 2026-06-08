package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Diagnosis.DiagnosisCreateDTO;
import Backend.ms_clasificator.DTOs.Diagnosis.DiagnosisResponseDTO;
import Backend.ms_clasificator.DTOs.Diagnosis.DiagnosisSummaryDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.DiagnosisMappers.DiagnosisMapper;
import Backend.ms_clasificator.Models.ClinicalRecord;
import Backend.ms_clasificator.Models.Diagnosis;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Repositories.ClinicalRecordRepository;
import Backend.ms_clasificator.Repositories.DiagnosisRepository;
import Backend.ms_clasificator.Repositories.MedicalDiagnosticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiagnosisService {

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Autowired
    private DiagnosisMapper diagnosisMapper;

    @Transactional(readOnly = true)
    public ApiResponse<List<DiagnosisSummaryDTO>> findAll() {
        List<DiagnosisSummaryDTO> diagnosis = this.diagnosisRepository.findAll()
                .stream()
                .map(diagnosisMapper::toSummaryDTO)
                .toList();

        return ApiResponse.success(diagnosis, "Diagnosticos obtenidos exitosamente");

    }

    @Transactional(readOnly = true)
    public ApiResponse<DiagnosisResponseDTO> findById(Integer id) {
        try {
            DiagnosisResponseDTO diagnosis = diagnosisRepository.findById(id)
                    .map(diagnosisMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnosis no encontrado con ID: " + id));

            return ApiResponse.success(diagnosis, "Diagnosis encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    //Metodo fundamental, cargamos los diagnosticos que ha dado un medico en una visita medica.
    @Transactional(readOnly = true)
    public ApiResponse<List<DiagnosisSummaryDTO>> findByClinicalRecordId(Integer clinicalRecordId) {

        if(!clinicalRecordRepository.existsById(clinicalRecordId)){
            return ApiResponse.error("Clinical Record no encontrado con ID: " + clinicalRecordId);
        }

        List<DiagnosisSummaryDTO> diagnosis = diagnosisRepository.findByClinicalRecordId(clinicalRecordId)
                .stream()
                .map(diagnosisMapper::toSummaryDTO)
                .toList();
        if (diagnosis.isEmpty()){
            return ApiResponse.success(diagnosis, "No se encontraron Diagnosis para el Clinical Record ID: " + clinicalRecordId);
        }

        return ApiResponse.success(diagnosis, "Diagnosis encontrado por Clinical Record");

    }

    public ApiResponse<DiagnosisResponseDTO> create(DiagnosisCreateDTO dto) {
        try {
            if (dto == null) return ApiResponse.error("El DTO no puede ser nulo");

            // Validamos que exista el clinicalRecord
            ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(dto.getClinicalRecordId())
                    .orElseThrow(() -> new IllegalArgumentException("Clinical Record no encontrado con ID: " + dto.getClinicalRecordId()));

            // Validamos que exista el medicalDiagnostic
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(dto.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException("Medical Diagnostic no encontrado con ID: " + dto.getMedicalDiagnosticId()));

            // Generamos la entidad vacia
            Diagnosis diagnosis = diagnosisMapper.toEntity(dto);

            // Asignamos las relaciones
            diagnosis.setClinicalRecord(clinicalRecord);
            diagnosis.setMedicalDiagnostic(medicalDiagnostic);

            return ApiResponse.success(diagnosisMapper.toResponseDTO(diagnosisRepository.save(diagnosis)), "Diagnosis creado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            Diagnosis diagnosis = diagnosisRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnosis no encontrado con ID: " + id));

            diagnosisRepository.delete(diagnosis);
            return ApiResponse.success("Diagnosis eliminado exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violacion a integridad de la base de datos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

}

