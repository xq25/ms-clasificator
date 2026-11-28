package backend.msclasificator.medicalimage.infrastructure.persistence;

import backend.msclasificator.medicalimage.application.port.out.MedicalImageTypeRepository;
import backend.msclasificator.medicalimage.domain.MedicalImageType;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class MedicalImageTypePersistenceAdapter implements MedicalImageTypeRepository {

    private final MedicalImageTypeJpaRepository jpa;

    MedicalImageTypePersistenceAdapter(MedicalImageTypeJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<MedicalImageType> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAllByActiveTrue(PageMapper.toPageable(query)));
    }

    @Override
    public PageResult<MedicalImageType> searchByName(String query, PageQuery pageQuery) {
        return PageMapper.toResult(jpa.findByNameContainingIgnoreCaseAndActiveTrue(query, PageMapper.toPageable(pageQuery)));
    }

    @Override
    public Optional<MedicalImageType> findById(Integer id) {
        return jpa.findByIdAndActiveTrue(id);
    }

    @Override
    public Optional<MedicalImageType> findByIdIncludingRetired(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public List<MedicalImageType> findByEvaluationAreaId(Integer evaluationAreaId) {
        return jpa.findByEvaluationAreaIdAndActiveTrue(evaluationAreaId);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsByIdAndActiveTrue(id);
    }

    @Override
    public boolean existsByIdIncludingRetired(Integer id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpa.existsByNameAndActiveTrue(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Integer id) {
        return jpa.existsByNameAndIdNotAndActiveTrue(name, id);
    }

    @Override
    public boolean existsByEvaluationAreaId(Integer evaluationAreaId) {
        return jpa.existsByEvaluationAreaIdAndActiveTrue(evaluationAreaId);
    }

    @Override
    public long count() {
        return jpa.countByActiveTrue();
    }

    @Override
    public MedicalImageType save(MedicalImageType medicalImageType) {
        return jpa.save(medicalImageType);
    }

    @Override
    public void delete(MedicalImageType medicalImageType) {
        jpa.delete(medicalImageType);
    }
}
