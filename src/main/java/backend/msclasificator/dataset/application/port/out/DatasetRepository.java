package backend.msclasificator.dataset.application.port.out;

import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface DatasetRepository {

    PageResult<Dataset> findAll(PageQuery query);

    Optional<Dataset> findById(Integer id);

    List<Dataset> findByMedicalDiagnosticId(Integer medicalDiagnosticId);

    Optional<Dataset> findByMedicalImageTypeId(Integer medicalImageTypeId);

    boolean existsById(Integer id);

    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    boolean existsByMedicalImageTypeId(Integer medicalImageTypeId);

    boolean existsByName(String name);

    // Variantes que excluyen al propio dataset, para validar duplicados al actualizar
    boolean existsByNameAndIdNot(String name, Integer id);

    boolean existsByMedicalDiagnosticIdAndIdNot(Integer medicalDiagnosticId, Integer id);

    boolean existsByMedicalImageTypeIdAndIdNot(Integer medicalImageTypeId, Integer id);

    long count();

    Dataset save(Dataset dataset);

    void delete(Dataset dataset);
}
