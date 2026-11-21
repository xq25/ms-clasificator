package backend.msclasificator.patient.application.port.out;

import backend.msclasificator.patient.domain.Patient;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.Optional;

public interface PatientRepository {

    PageResult<Patient> findAll(PageQuery query);

    // Coincidencia parcial, sin distinguir mayúsculas/minúsculas
    PageResult<Patient> searchByDocument(String query, PageQuery pageQuery);

    Optional<Patient> findById(Integer id);

    Optional<Patient> findByDocument(String document);

    Optional<Patient> findByUserId(String userId);

    boolean existsById(Integer id);

    long count();

    Patient save(Patient patient);

    void delete(Patient patient);
}
