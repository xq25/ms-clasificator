package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.MedicalDiagnostic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicalDiagnosticRepository extends JpaRepository<MedicalDiagnostic, Integer> {
    MedicalDiagnostic findByDiagnosticCode(String diagnosticCode);
    List<MedicalDiagnostic> findByParentDiagnosticId(Integer parentDiagnosticId);
    boolean existsByIdAndParentDiagnosticId(
            Integer subDiagnosticId,
            Integer parentDiagnosticId
    );
    boolean existsByParentDiagnosticId(Integer id);

    @Query("SELECT COUNT(m) FROM MedicalDiagnostic m")
    long countAll();
}
