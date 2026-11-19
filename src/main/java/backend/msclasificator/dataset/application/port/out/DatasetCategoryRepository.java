package backend.msclasificator.dataset.application.port.out;

import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface DatasetCategoryRepository {

    PageResult<DatasetCategory> findAll(PageQuery query);

    Optional<DatasetCategory> findById(Integer id);

    List<DatasetCategory> findByDatasetId(Integer datasetId);

    boolean existsById(Integer id);

    long count();

    DatasetCategory save(DatasetCategory datasetCategory);

    void delete(DatasetCategory datasetCategory);
}
