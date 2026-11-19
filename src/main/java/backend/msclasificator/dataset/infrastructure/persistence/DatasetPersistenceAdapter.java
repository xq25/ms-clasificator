package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.application.port.out.DatasetRepository;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class DatasetPersistenceAdapter implements DatasetRepository {

    private final DatasetJpaRepository jpa;

    DatasetPersistenceAdapter(DatasetJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<Dataset> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public Optional<Dataset> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public List<Dataset> findByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        return jpa.findByMedicalDiagnosticId(medicalDiagnosticId);
    }

    @Override
    public Optional<Dataset> findByMedicalImageTypeId(Integer medicalImageTypeId) {
        return jpa.findByMedicalImageTypeId(medicalImageTypeId);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        return jpa.existsByMedicalDiagnosticId(medicalDiagnosticId);
    }

    @Override
    public boolean existsByMedicalImageTypeId(Integer medicalImageTypeId) {
        return jpa.existsByMedicalImageTypeId(medicalImageTypeId);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public Dataset save(Dataset dataset) {
        return jpa.save(dataset);
    }

    @Override
    public void delete(Dataset dataset) {
        jpa.delete(dataset);
    }
}
