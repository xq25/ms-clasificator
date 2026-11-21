package backend.msclasificator.doctor.application.port.out;

import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {

    PageResult<Doctor> findAll(PageQuery query);

    // Coincidencia parcial, sin distinguir mayúsculas/minúsculas
    PageResult<Doctor> searchByCode(String query, PageQuery pageQuery);

    Optional<Doctor> findById(Integer id);

    Optional<Doctor> findByCode(String code);

    Optional<Doctor> findByUserId(String userId);

    // Doctores que aún no tienen asignada el área de evaluación indicada
    List<Doctor> findDoctorsNotAssignedToEvaluationArea(Integer evaluationAreaId);

    boolean existsById(Integer id);

    boolean existsByCode(String code);

    boolean existsByUserId(String userId);

    long count();

    Doctor save(Doctor doctor);

    void delete(Doctor doctor);
}
