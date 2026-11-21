package backend.msclasificator.doctor.infrastructure.persistence;

import backend.msclasificator.doctor.application.port.out.DoctorRepository;
import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class DoctorPersistenceAdapter implements DoctorRepository {

    private final DoctorJpaRepository jpa;

    DoctorPersistenceAdapter(DoctorJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<Doctor> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public PageResult<Doctor> searchByCode(String query, PageQuery pageQuery) {
        return PageMapper.toResult(jpa.findByCodeContainingIgnoreCase(query, PageMapper.toPageable(pageQuery)));
    }

    @Override
    public Optional<Doctor> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Doctor> findByCode(String code) {
        return jpa.findByCode(code);
    }

    @Override
    public Optional<Doctor> findByUserId(String userId) {
        return jpa.findByUserId(userId);
    }

    @Override
    public List<Doctor> findDoctorsNotAssignedToEvaluationArea(Integer evaluationAreaId) {
        return jpa.findDoctorsNotAssignedToEvaluationArea(evaluationAreaId);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpa.existsByCode(code);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return jpa.existsByUserId(userId);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public Doctor save(Doctor doctor) {
        return jpa.save(doctor);
    }

    @Override
    public void delete(Doctor doctor) {
        jpa.delete(doctor);
    }
}
