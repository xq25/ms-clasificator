package backend.msclasificator.clinicalrecord.infrastructure.persistence;

import backend.msclasificator.clinicalrecord.application.port.out.ClinicalRecordRepository;
import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class ClinicalRecordPersistenceAdapter implements ClinicalRecordRepository {

    private final ClinicalRecordJpaRepository jpa;

    ClinicalRecordPersistenceAdapter(ClinicalRecordJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<ClinicalRecord> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public PageResult<ClinicalRecord> findByPatientId(Integer patientId, PageQuery query) {
        return PageMapper.toResult(jpa.findByPatientId(patientId, PageMapper.toPageable(query)));
    }

    @Override
    public Optional<ClinicalRecord> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsById(id);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public ClinicalRecord save(ClinicalRecord clinicalRecord) {
        return jpa.save(clinicalRecord);
    }

    @Override
    public void delete(ClinicalRecord clinicalRecord) {
        jpa.delete(clinicalRecord);
    }
}
