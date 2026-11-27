package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.domain.Dataset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DatasetJpaRepository extends JpaRepository<Dataset, Integer> {

    Page<Dataset> findAllByActiveTrue(Pageable pageable);

    Optional<Dataset> findByIdAndActiveTrue(Integer id);

    List<Dataset> findByMedicalDiagnosticIdAndActiveTrue(Integer medicalDiagnosticId);

    Optional<Dataset> findByMedicalImageTypeIdAndActiveTrue(Integer medicalImageTypeId);

    boolean existsByIdAndActiveTrue(Integer id);

    boolean existsByMedicalDiagnosticIdAndActiveTrue(Integer medicalDiagnosticId);

    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    boolean existsByMedicalImageTypeIdAndActiveTrue(Integer medicalImageTypeId);

    boolean existsByMedicalImageTypeId(Integer medicalImageTypeId);

    boolean existsByNameAndActiveTrue(String name);

    boolean existsByNameAndIdNotAndActiveTrue(String name, Integer id);

    boolean existsByMedicalDiagnosticIdAndIdNotAndActiveTrue(Integer medicalDiagnosticId, Integer id);

    boolean existsByMedicalImageTypeIdAndIdNotAndActiveTrue(Integer medicalImageTypeId, Integer id);

    long countByActiveTrue();
}
