package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.domain.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DatasetJpaRepository extends JpaRepository<Dataset, Integer> {
    List<Dataset> findByMedicalDiagnosticId(Integer medicalDiagnosticId); //pa buscar todas las configs asociadas a un diagnóstico
    Optional<Dataset> findByMedicalImageTypeId(Integer medicalImageTypeId);
    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);  // pa validar antes de crear duplicados
    boolean existsByMedicalImageTypeId(Integer medicalImageTypeId); // pa validar antes de crear duplicados
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
    boolean existsByMedicalDiagnosticIdAndIdNot(Integer medicalDiagnosticId, Integer id);
    boolean existsByMedicalImageTypeIdAndIdNot(Integer medicalImageTypeId, Integer id);

    @Query("SELECT COUNT(d) FROM Dataset d")
    long countAll();
}