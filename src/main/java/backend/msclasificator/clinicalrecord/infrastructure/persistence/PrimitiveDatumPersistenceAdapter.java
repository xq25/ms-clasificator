package backend.msclasificator.clinicalrecord.infrastructure.persistence;

import backend.msclasificator.clinicalrecord.application.port.out.PrimitiveDatumRepository;
import backend.msclasificator.clinicalrecord.domain.PrimitiveDatum;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class PrimitiveDatumPersistenceAdapter implements PrimitiveDatumRepository {

    private final PrimitiveDatumJpaRepository jpa;

    PrimitiveDatumPersistenceAdapter(PrimitiveDatumJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<PrimitiveDatum> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public Optional<PrimitiveDatum> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsById(id);
    }

    @Override
    public Optional<PrimitiveDatum> findByNameIgnoreCase(String name) {
        return Optional.ofNullable(jpa.findByNameIgnoreCase(name));
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public PrimitiveDatum save(PrimitiveDatum primitiveDatum) {
        return jpa.save(primitiveDatum);
    }

    @Override
    public void delete(PrimitiveDatum primitiveDatum) {
        jpa.delete(primitiveDatum);
    }
}
