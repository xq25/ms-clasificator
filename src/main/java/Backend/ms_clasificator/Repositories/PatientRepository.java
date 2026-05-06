package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Patient findByDocument(String document);
}
