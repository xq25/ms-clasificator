package backend.msclasificator.doctor.infrastructure.persistence;

import backend.msclasificator.doctor.application.port.out.DoctorAreaRepository;
import backend.msclasificator.doctor.domain.DoctorArea;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class DoctorAreaPersistenceAdapter implements DoctorAreaRepository {

    private final DoctorAreaJpaRepository jpa;

    DoctorAreaPersistenceAdapter(DoctorAreaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<DoctorArea> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public Optional<DoctorArea> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public List<DoctorArea> findByDoctorId(Integer doctorId) {
        return jpa.findByDoctorId(doctorId);
    }

    @Override
    public List<DoctorArea> findByEvaluationAreaId(Integer evaluationAreaId) {
        return jpa.findByEvaluationAreaId(evaluationAreaId);
    }

    @Override
    public Optional<DoctorArea> findByDoctorIdAndEvaluationAreaId(Integer doctorId, Integer evaluationAreaId) {
        return Optional.ofNullable(jpa.findByDoctorIdAndEvaluationAreaId(doctorId, evaluationAreaId));
    }

    @Override
    public boolean existsByDoctorIdAndEvaluationAreaId(Integer doctorId, Integer evaluationAreaId) {
        return jpa.existsByDoctorIdAndEvaluationAreaId(doctorId, evaluationAreaId);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public DoctorArea save(DoctorArea doctorArea) {
        return jpa.save(doctorArea);
    }

    @Override
    public void delete(DoctorArea doctorArea) {
        jpa.delete(doctorArea);
    }
}
