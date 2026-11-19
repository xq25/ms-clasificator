package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.domain.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DatasetRepository extends JpaRepository<Dataset, Integer> {
    List<Dataset> findByMedicalDiagnosticId(Integer medicalDiagnosticId); //pa buscar todas las configs asociadas a un diagnóstico
    Optional<Dataset> findByMedicalImageTypeId(Integer medicalImageTypeId);
    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);  // pa validar antes de crear duplicados
    boolean existsByMedicalImageTypeId(Integer medicalImageTypeId); // pa validar antes de crear duplicados

    @Query("SELECT COUNT(d) FROM Dataset d")
    long countAll();
}