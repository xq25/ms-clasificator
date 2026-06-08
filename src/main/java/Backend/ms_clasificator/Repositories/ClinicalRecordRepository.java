package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.ClinicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, Integer> {
    List<ClinicalRecord> findByPatientId(Integer patientId);
}
