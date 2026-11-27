package backend.msclasificator.clinicalrecord.application.port.out;

import backend.msclasificator.clinicalrecord.domain.Diagnosis;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.Optional;

public interface DiagnosisRepository {

    PageResult<Diagnosis> findAll(PageQuery query);

    PageResult<Diagnosis> findByClinicalRecordId(Integer clinicalRecordId, PageQuery query);

    Optional<Diagnosis> findById(Integer id);

    long count();

    // Diagnósticos de historias clínicas que usan este diagnóstico CIE-10 (bug #17)
    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    Diagnosis save(Diagnosis diagnosis);

    void delete(Diagnosis diagnosis);
}
