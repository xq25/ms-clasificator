package backend.msclasificator.medicalimage.infrastructure.persistence;

import backend.msclasificator.medicalimage.domain.MedicalImageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicalImageTypeJpaRepository extends JpaRepository<MedicalImageType, Integer> {

	MedicalImageType findByName(String name);

	List<MedicalImageType> findByEvaluationAreaId(Integer evaluationAreaId);

	boolean existsByEvaluationAreaId(Integer evaluationAreaId);

	boolean existsByName(String name);

	@Query("SELECT COUNT(m) FROM MedicalImageType m")
	long countAll();

	Page<MedicalImageType> findByNameContainingIgnoreCase(String query, Pageable pageable);
}
