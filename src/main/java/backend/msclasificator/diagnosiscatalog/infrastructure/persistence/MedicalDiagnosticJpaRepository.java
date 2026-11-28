package backend.msclasificator.diagnosiscatalog.infrastructure.persistence;

import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicalDiagnosticJpaRepository extends JpaRepository<MedicalDiagnostic, Integer> {
    // Incluye retirados: el código CIE-10 es único entre todos (ADR-013)
    MedicalDiagnostic findByDiagnosticCode(String diagnosticCode);

    // Soft delete (ADR-013): el resto de lecturas del adaptador son solo de activos
    Page<MedicalDiagnostic> findAllByActiveTrue(Pageable pageable);

    Optional<MedicalDiagnostic> findByIdAndActiveTrue(Integer id);

    boolean existsByIdAndActiveTrue(Integer id);

    List<MedicalDiagnostic> findByParentDiagnosticIdAndActiveTrue(Integer parentDiagnosticId);

    long countByParentDiagnosticIdAndActiveTrue(Integer parentDiagnosticId);

    long countByActiveTrue();

    Page<MedicalDiagnostic> findByDiagnosticNameContainingIgnoreCaseAndActiveTrue(String query, Pageable pageable);

    Page<MedicalDiagnostic> findByDiagnosticCodeContainingIgnoreCaseAndActiveTrue(String query, Pageable pageable);
}
