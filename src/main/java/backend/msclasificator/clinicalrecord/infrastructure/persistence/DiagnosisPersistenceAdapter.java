package backend.msclasificator.clinicalrecord.infrastructure.persistence;

import backend.msclasificator.clinicalrecord.application.port.out.DiagnosisRepository;
import backend.msclasificator.clinicalrecord.domain.Diagnosis;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class DiagnosisPersistenceAdapter implements DiagnosisRepository {

    private final DiagnosisJpaRepository jpa;

    DiagnosisPersistenceAdapter(DiagnosisJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<Diagnosis> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public PageResult<Diagnosis> findByClinicalRecordId(Integer clinicalRecordId, PageQuery query) {
        return PageMapper.toResult(jpa.findByClinicalRecordId(clinicalRecordId, PageMapper.toPageable(query)));
    }

    @Override
    public Optional<Diagnosis> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        return jpa.existsByMedicalDiagnosticId(medicalDiagnosticId);
    }

    @Override
    public Diagnosis save(Diagnosis diagnosis) {
        return jpa.save(diagnosis);
    }

    @Override
    public void delete(Diagnosis diagnosis) {
        jpa.delete(diagnosis);
    }
}
