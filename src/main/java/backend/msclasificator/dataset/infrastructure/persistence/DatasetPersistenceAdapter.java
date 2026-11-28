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
        return PageMapper.toResult(jpa.findAllByActiveTrue(PageMapper.toPageable(query)));
    }

    @Override
    public Optional<Dataset> findById(Integer id) {
        return jpa.findByIdAndActiveTrue(id);
    }

    @Override
    public List<Dataset> findByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        return jpa.findByMedicalDiagnosticIdAndActiveTrue(medicalDiagnosticId);
    }

    @Override
    public Optional<Dataset> findByMedicalImageTypeId(Integer medicalImageTypeId) {
        return jpa.findByMedicalImageTypeIdAndActiveTrue(medicalImageTypeId);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsByIdAndActiveTrue(id);
    }

    @Override
    public boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        return jpa.existsByMedicalDiagnosticIdAndActiveTrue(medicalDiagnosticId);
    }

    @Override
    public boolean existsByMedicalDiagnosticIdIncludingRetired(Integer medicalDiagnosticId) {
        return jpa.existsByMedicalDiagnosticId(medicalDiagnosticId);
    }

    @Override
    public boolean existsByMedicalImageTypeId(Integer medicalImageTypeId) {
        return jpa.existsByMedicalImageTypeIdAndActiveTrue(medicalImageTypeId);
    }

    @Override
    public boolean existsByName(String name) {
        return jpa.existsByNameAndActiveTrue(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Integer id) {
        return jpa.existsByNameAndIdNotAndActiveTrue(name, id);
    }

    @Override
    public boolean existsByMedicalDiagnosticIdAndIdNot(Integer medicalDiagnosticId, Integer id) {
        return jpa.existsByMedicalDiagnosticIdAndIdNotAndActiveTrue(medicalDiagnosticId, id);
    }

    @Override
    public boolean existsByMedicalImageTypeIdAndIdNot(Integer medicalImageTypeId, Integer id) {
        return jpa.existsByMedicalImageTypeIdAndIdNotAndActiveTrue(medicalImageTypeId, id);
    }

    @Override
    public long count() {
        return jpa.countByActiveTrue();
    }

    @Override
    public Dataset save(Dataset dataset) {
        return jpa.save(dataset);
    }
}
