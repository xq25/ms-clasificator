package backend.msclasificator.doctor.application.port.out;

import backend.msclasificator.doctor.domain.DoctorArea;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface DoctorAreaRepository {

    PageResult<DoctorArea> findAll(PageQuery query);

    Optional<DoctorArea> findById(Integer id);

    List<DoctorArea> findByDoctorId(Integer doctorId);

    List<DoctorArea> findByEvaluationAreaId(Integer evaluationAreaId);

    Optional<DoctorArea> findByDoctorIdAndEvaluationAreaId(Integer doctorId, Integer evaluationAreaId);

    boolean existsByDoctorIdAndEvaluationAreaId(Integer doctorId, Integer evaluationAreaId);

    long count();

    DoctorArea save(DoctorArea doctorArea);

    void delete(DoctorArea doctorArea);
}
