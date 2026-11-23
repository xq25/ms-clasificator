package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.domain.DatasetCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DatasetCategoryJpaRepository extends JpaRepository<DatasetCategory, Integer> {

    Page<DatasetCategory> findAllByActiveTrue(Pageable pageable);

    Optional<DatasetCategory> findByIdAndActiveTrue(Integer id);

    List<DatasetCategory> findByDatasetIdAndActiveTrue(Integer datasetId);

    boolean existsByIdAndActiveTrue(Integer id);

    long countByActiveTrue();

    long countByDatasetId(Integer datasetId);

    boolean existsByDatasetId(Integer datasetId);
}
