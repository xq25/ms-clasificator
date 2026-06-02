package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.DatasetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatasetCategoryRepository extends JpaRepository<DatasetCategory, Integer> {
    List<DatasetCategory> findByDatasetId(Integer datasetId); //pa traer todos los estados de una config
}