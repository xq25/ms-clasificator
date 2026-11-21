package backend.msclasificator.doctor.infrastructure.persistence;

import backend.msclasificator.doctor.application.port.out.EvaluationAreaRepository;
import backend.msclasificator.doctor.domain.EvaluationArea;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class EvaluationAreaPersistenceAdapter implements EvaluationAreaRepository {

    private final EvaluationAreaJpaRepository jpa;

    EvaluationAreaPersistenceAdapter(EvaluationAreaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<EvaluationArea> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public PageResult<EvaluationArea> searchByName(String query, PageQuery pageQuery) {
        return PageMapper.toResult(jpa.findByNameContainingIgnoreCase(query, PageMapper.toPageable(pageQuery)));
    }

    @Override
    public PageResult<EvaluationArea> searchByCode(String query, PageQuery pageQuery) {
        return PageMapper.toResult(jpa.findByCodeAreaContainingIgnoreCase(query, PageMapper.toPageable(pageQuery)));
    }

    @Override
    public Optional<EvaluationArea> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<EvaluationArea> findByName(String name) {
        return Optional.ofNullable(jpa.findByName(name));
    }

    @Override
    public Optional<EvaluationArea> findByCodeArea(String codeArea) {
        return Optional.ofNullable(jpa.findByCodeArea(codeArea));
    }

    @Override
    public List<EvaluationArea> findEvaluationAreasNotAssignedToDoctor(Integer doctorId) {
        return jpa.findEvaluationAreasNotAssignedToDoctor(doctorId);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpa.existsByName(name);
    }

    @Override
    public boolean existsByCodeArea(String codeArea) {
        return jpa.existsByCodeArea(codeArea);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public EvaluationArea save(EvaluationArea evaluationArea) {
        return jpa.save(evaluationArea);
    }

    @Override
    public void delete(EvaluationArea evaluationArea) {
        jpa.delete(evaluationArea);
    }
}
