package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.MedicalDiagnostic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalDiagnosticRepository extends JpaRepository<MedicalDiagnostic, Integer> {
    MedicalDiagnostic findByDiagnosticCode(String diagnosticCode);
}
