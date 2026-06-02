package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Integer> {
    Diagnosis findByClinicalRecordId(Integer clinicalRecordId);
}
