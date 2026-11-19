package backend.msclasificator.diagnosiscatalog.infrastructure.persistence;

import backend.msclasificator.diagnosiscatalog.application.port.out.MedicalDiagnosticRepository;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class MedicalDiagnosticPersistenceAdapter implements MedicalDiagnosticRepository {

    private final MedicalDiagnosticJpaRepository jpa;

    MedicalDiagnosticPersistenceAdapter(MedicalDiagnosticJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<MedicalDiagnostic> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public PageResult<MedicalDiagnostic> searchByName(String query, PageQuery pageQuery) {
        return PageMapper.toResult(jpa.findByDiagnosticNameContainingIgnoreCase(query, PageMapper.toPageable(pageQuery)));
    }

    @Override
    public PageResult<MedicalDiagnostic> searchByCode(String query, PageQuery pageQuery) {
        return PageMapper.toResult(jpa.findByDiagnosticCodeContainingIgnoreCase(query, PageMapper.toPageable(pageQuery)));
    }

    @Override
    public Optional<MedicalDiagnostic> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<MedicalDiagnostic> findByDiagnosticCode(String diagnosticCode) {
        return Optional.ofNullable(jpa.findByDiagnosticCode(diagnosticCode));
    }

    @Override
    public List<MedicalDiagnostic> findByParentDiagnosticId(Integer parentDiagnosticId) {
        return jpa.findByParentDiagnosticId(parentDiagnosticId);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByParentDiagnosticId(Integer parentDiagnosticId) {
        return jpa.existsByParentDiagnosticId(parentDiagnosticId);
    }

    @Override
    public boolean existsByIdAndParentDiagnosticId(Integer subDiagnosticId, Integer parentDiagnosticId) {
        return jpa.existsByIdAndParentDiagnosticId(subDiagnosticId, parentDiagnosticId);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public MedicalDiagnostic save(MedicalDiagnostic medicalDiagnostic) {
        return jpa.save(medicalDiagnostic);
    }

    @Override
    public void delete(MedicalDiagnostic medicalDiagnostic) {
        jpa.delete(medicalDiagnostic);
    }
}
