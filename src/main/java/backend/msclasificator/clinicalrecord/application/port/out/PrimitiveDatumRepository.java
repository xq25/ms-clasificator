package backend.msclasificator.clinicalrecord.application.port.out;

import backend.msclasificator.clinicalrecord.domain.PrimitiveDatum;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.Optional;

public interface PrimitiveDatumRepository {

    PageResult<PrimitiveDatum> findAll(PageQuery query);

    Optional<PrimitiveDatum> findById(Integer id);

    boolean existsById(Integer id);

    // Coincidencia exacta del nombre, sin distinguir mayúsculas/minúsculas
    Optional<PrimitiveDatum> findByNameIgnoreCase(String name);

    long count();

    PrimitiveDatum save(PrimitiveDatum primitiveDatum);

    void delete(PrimitiveDatum primitiveDatum);
}
