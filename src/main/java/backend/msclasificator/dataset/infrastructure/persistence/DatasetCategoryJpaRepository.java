package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.domain.DatasetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DatasetCategoryJpaRepository extends JpaRepository<DatasetCategory, Integer> {
    List<DatasetCategory> findByDatasetId(Integer datasetId); //pa traer todos los estados de una config

    @Query("SELECT COUNT(dc) FROM DatasetCategory dc")
    long countAll();
}