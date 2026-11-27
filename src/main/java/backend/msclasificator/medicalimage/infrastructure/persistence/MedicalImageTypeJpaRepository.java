package backend.msclasificator.medicalimage.infrastructure.persistence;

import backend.msclasificator.medicalimage.domain.MedicalImageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicalImageTypeJpaRepository extends JpaRepository<MedicalImageType, Integer> {

	MedicalImageType findByName(String name);

	// Soft delete (ADR-013): las consultas del adaptador leen solo activos salvo las de id "IncludingRetired"
	Page<MedicalImageType> findAllByActiveTrue(Pageable pageable);

	Optional<MedicalImageType> findByIdAndActiveTrue(Integer id);

	boolean existsByIdAndActiveTrue(Integer id);

	List<MedicalImageType> findByEvaluationAreaIdAndActiveTrue(Integer evaluationAreaId);

	boolean existsByEvaluationAreaIdAndActiveTrue(Integer evaluationAreaId);

	boolean existsByNameAndActiveTrue(String name);

	boolean existsByNameAndIdNotAndActiveTrue(String name, Integer id);

	long countByActiveTrue();

	Page<MedicalImageType> findByNameContainingIgnoreCaseAndActiveTrue(String query, Pageable pageable);
}
