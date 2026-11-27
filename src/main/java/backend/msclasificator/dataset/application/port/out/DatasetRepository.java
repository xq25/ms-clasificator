package backend.msclasificator.dataset.application.port.out;

import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

// Todas las lecturas y validaciones consideran solo datasets activos (ADR-011). Retirar = retire() + save().
public interface DatasetRepository {

    PageResult<Dataset> findAll(PageQuery query);

    Optional<Dataset> findById(Integer id);

    List<Dataset> findByMedicalDiagnosticId(Integer medicalDiagnosticId);

    Optional<Dataset> findByMedicalImageTypeId(Integer medicalImageTypeId);

    boolean existsById(Integer id);

    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    // Incluye retirados (ADR-011): un diagnóstico que fue padre de un dataset no se puede borrar del catálogo (bug #17)
    boolean existsByMedicalDiagnosticIdIncludingRetired(Integer medicalDiagnosticId);

    boolean existsByMedicalImageTypeId(Integer medicalImageTypeId);

    // Incluye retirados (ADR-011): un tipo de imagen con datasets no se puede borrar (bug #23)
    boolean existsByMedicalImageTypeIdIncludingRetired(Integer medicalImageTypeId);

    boolean existsByName(String name);

    // Variantes que excluyen al propio dataset, para validar duplicados al actualizar
    boolean existsByNameAndIdNot(String name, Integer id);

    boolean existsByMedicalDiagnosticIdAndIdNot(Integer medicalDiagnosticId, Integer id);

    boolean existsByMedicalImageTypeIdAndIdNot(Integer medicalImageTypeId, Integer id);

    long count();

    Dataset save(Dataset dataset);
}
