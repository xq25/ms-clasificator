package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.application.port.out.DatasetCategoryRepository;
import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class DatasetCategoryPersistenceAdapter implements DatasetCategoryRepository {

    private final DatasetCategoryJpaRepository jpa;

    DatasetCategoryPersistenceAdapter(DatasetCategoryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<DatasetCategory> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAllByActiveTrue(PageMapper.toPageable(query)));
    }

    @Override
    public Optional<DatasetCategory> findById(Integer id) {
        return jpa.findByIdAndActiveTrue(id);
    }

    @Override
    public List<DatasetCategory> findByDatasetId(Integer datasetId) {
        return jpa.findByDatasetIdAndActiveTrue(datasetId);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsByIdAndActiveTrue(id);
    }

    @Override
    public long count() {
        return jpa.countByActiveTrue();
    }

    @Override
    public long countByDatasetIdIncludingRetired(Integer datasetId) {
        return jpa.countByDatasetId(datasetId);
    }

    @Override
    public boolean existsByDatasetIdIncludingRetired(Integer datasetId) {
        return jpa.existsByDatasetId(datasetId);
    }

    @Override
    public DatasetCategory save(DatasetCategory datasetCategory) {
        return jpa.save(datasetCategory);
    }
}
