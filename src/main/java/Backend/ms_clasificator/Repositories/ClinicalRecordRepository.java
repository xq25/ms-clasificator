package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.ClinicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, Integer> {
    List<ClinicalRecord> findByPatientId(Integer patientId);
    Page<ClinicalRecord> findByPatientId(Integer patientId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM ClinicalRecord c")
    long countAll();
}
