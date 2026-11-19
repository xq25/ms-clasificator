package backend.msclasificator.patient.infrastructure.persistence;

import backend.msclasificator.patient.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByDocument(String document);
    Optional<Patient> findByUserId(String userId);

    @Query("SELECT COUNT(p) FROM Patient p")
    long countAll();

    Page<Patient> findByDocumentContainingIgnoreCase(String query, Pageable pageable);
}
