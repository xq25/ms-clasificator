package backend.msclasificator.dataset.application.port.out;

import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

// Las lecturas consideran solo categorías activas, salvo los métodos "IncludingRetired" (ADR-011).
public interface DatasetCategoryRepository {

    PageResult<DatasetCategory> findAll(PageQuery query);

    Optional<DatasetCategory> findById(Integer id);

    List<DatasetCategory> findByDatasetId(Integer datasetId);

    boolean existsById(Integer id);

    long count();

    // Cuenta también las retiradas: el siguiente numValue nunca reutiliza el de una clase retirada
    long countByDatasetIdIncludingRetired(Integer datasetId);

    // Incluye retiradas: sus clasificaciones históricas siguen dependiendo del diagnóstico del dataset
    boolean existsByDatasetIdIncludingRetired(Integer datasetId);

    DatasetCategory save(DatasetCategory datasetCategory);
}
