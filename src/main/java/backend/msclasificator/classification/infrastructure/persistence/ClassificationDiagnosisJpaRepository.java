package backend.msclasificator.classification.infrastructure.persistence;

import backend.msclasificator.classification.domain.ClassificationDiagnosis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassificationDiagnosisJpaRepository extends JpaRepository<ClassificationDiagnosis, Integer> {
    List<ClassificationDiagnosis> findByImageDiagnosticId(Integer imageDIagnosticId);
    Page<ClassificationDiagnosis> findByImageDiagnosticId(Integer imageDiagnosticId, Pageable pageable);

    List<ClassificationDiagnosis> findByMedicalDiagnosticId(Integer medicalDiagnosticId);


    boolean existsByImageDiagnosticIdAndMedicalDiagnosticId(Integer imageDiagnosticId, Integer medicalDiagnosticId);

    @Query("SELECT COUNT(i) FROM ClassificationDiagnosis i")
    long countAll();
}
