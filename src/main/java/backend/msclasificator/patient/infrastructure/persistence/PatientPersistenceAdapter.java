package backend.msclasificator.patient.infrastructure.persistence;

import backend.msclasificator.patient.application.port.out.PatientRepository;
import backend.msclasificator.patient.domain.Patient;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class PatientPersistenceAdapter implements PatientRepository {

    private final PatientJpaRepository jpa;

    PatientPersistenceAdapter(PatientJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<Patient> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public PageResult<Patient> searchByDocument(String query, PageQuery pageQuery) {
        return PageMapper.toResult(jpa.findByDocumentContainingIgnoreCase(query, PageMapper.toPageable(pageQuery)));
    }

    @Override
    public Optional<Patient> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Patient> findByDocument(String document) {
        return jpa.findByDocument(document);
    }

    @Override
    public Optional<Patient> findByUserId(String userId) {
        return jpa.findByUserId(userId);
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
    public Patient save(Patient patient) {
        return jpa.save(patient);
    }

    @Override
    public void delete(Patient patient) {
        jpa.delete(patient);
    }
}
