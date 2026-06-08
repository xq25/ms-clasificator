package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByDocument(String document);
    Optional<Patient> findByUserId(String userId);
}
