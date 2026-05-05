package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.MedicalDiagnostic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalDiagnosticRepository extends JpaRepository<MedicalDiagnostic, Integer> {

     //buscar diagnostico por su codigo unico

     Optional<MedicalDiagnostic> findByDiagnosticCode(String diagnosticCode);

     // Verificar si ya existe ese código de diagnóstico
     boolean existsByDiagnosticCode(String diagnosticCode);
}
