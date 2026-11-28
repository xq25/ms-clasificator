package backend.msclasificator.clinicalrecord.application;

import backend.msclasificator.clinicalrecord.api.dto.DiagnosisCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.DiagnosisResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.DiagnosisSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.clinicalrecord.api.mapper.DiagnosisMapper;
import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
import backend.msclasificator.clinicalrecord.domain.Diagnosis;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.clinicalrecord.application.port.out.ClinicalRecordRepository;
import backend.msclasificator.clinicalrecord.application.port.out.DiagnosisRepository;
import backend.msclasificator.diagnosiscatalog.application.port.out.MedicalDiagnosticRepository;
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
    public ApiResponse<PagedResponse<DiagnosisSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<DiagnosisSummaryDTO> page = diagnosisRepository.findAll(pageRequest.toPageQuery())
                .map(diagnosisMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<DiagnosisSummaryDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Diagnosticos obtenidos exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(diagnosisRepository.count(), "Total de diagnósticos");
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

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<DiagnosisSummaryDTO>> findByClinicalRecordId(Integer clinicalRecordId, PageRequestDTO pageRequest) {

        if (!clinicalRecordRepository.existsById(clinicalRecordId)) {
            return ApiResponse.error("Clinical Record no encontrado con ID: " + clinicalRecordId);
        }

        PageResult<DiagnosisSummaryDTO> page = diagnosisRepository
                .findByClinicalRecordId(clinicalRecordId, pageRequest.toPageQuery())
                .map(diagnosisMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<DiagnosisSummaryDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                page.isEmpty()
                        ? "No se encontraron Diagnosis para el Clinical Record ID: " + clinicalRecordId
                        : "Diagnosis encontrado por Clinical Record"
        );
    }

    public ApiResponse<DiagnosisResponseDTO> create(DiagnosisCreateDTO dto) {
        try {
            if (dto == null) return ApiResponse.error("El DTO no puede ser nulo");

            // Validamos que exista el clinicalRecord
            ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(dto.getClinicalRecordId())
                    .orElseThrow(() -> new IllegalArgumentException("Clinical Record no encontrado con ID: " + dto.getClinicalRecordId()));

            // Validamos que exista el medicalDiagnostic
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findByIdIncludingRetired(dto.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException("Medical Diagnostic no encontrado con ID: " + dto.getMedicalDiagnosticId()));
            // Un diagnóstico retirado no se registra en historias clínicas nuevas (ADR-013)
            if (Boolean.FALSE.equals(medicalDiagnostic.getActive())) {
                return ApiResponse.error("El diagnóstico " + medicalDiagnostic.getDiagnosticCode() + " está retirado del catálogo");
            }

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

