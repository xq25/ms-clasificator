package backend.msclasificator.clinicalrecord.infrastructure.persistence;

import backend.msclasificator.clinicalrecord.domain.Diagnosis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiagnosisJpaRepository extends JpaRepository<Diagnosis, Integer> {
    List<Diagnosis> findByClinicalRecordId(Integer clinicalRecordId);
    Page<Diagnosis> findByClinicalRecordId(Integer clinicalRecordId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Diagnosis d")
    long countAll();
}
