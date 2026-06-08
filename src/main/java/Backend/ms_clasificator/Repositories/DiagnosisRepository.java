package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Integer> {
    List<Diagnosis> findByClinicalRecordId(Integer clinicalRecordId);
}
