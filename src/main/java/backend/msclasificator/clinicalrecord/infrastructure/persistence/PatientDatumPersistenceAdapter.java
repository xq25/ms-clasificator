package backend.msclasificator.clinicalrecord.infrastructure.persistence;

import backend.msclasificator.clinicalrecord.application.port.out.PatientDatumRepository;
import backend.msclasificator.clinicalrecord.domain.PatientDatum;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class PatientDatumPersistenceAdapter implements PatientDatumRepository {

    private final PatientDatumJpaRepository jpa;

    PatientDatumPersistenceAdapter(PatientDatumJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<PatientDatum> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public List<PatientDatum> findByClinicalRecordId(Integer clinicalRecordId) {
        return jpa.findByClinicalRecordId(clinicalRecordId);
    }

    @Override
    public List<PatientDatum> findByPrimitiveDatumId(Integer primitiveDatumId) {
        return jpa.findByPrimitiveDatumId(primitiveDatumId);
    }

    @Override
    public Optional<PatientDatum> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public PatientDatum save(PatientDatum patientDatum) {
        return jpa.save(patientDatum);
    }

    @Override
    public void delete(PatientDatum patientDatum) {
        jpa.delete(patientDatum);
    }
}
