package backend.msclasificator.clinicalrecord.application.port.out;

import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.Optional;

public interface ClinicalRecordRepository {

    PageResult<ClinicalRecord> findAll(PageQuery query);

    PageResult<ClinicalRecord> findByPatientId(Integer patientId, PageQuery query);

    Optional<ClinicalRecord> findById(Integer id);

    boolean existsById(Integer id);

    long count();

    ClinicalRecord save(ClinicalRecord clinicalRecord);

    void delete(ClinicalRecord clinicalRecord);
}
