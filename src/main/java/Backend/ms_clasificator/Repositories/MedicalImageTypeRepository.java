package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.MedicalImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalImageTypeRepository extends JpaRepository<MedicalImageType, Integer> {

	MedicalImageType findByName(String name);

	List<MedicalImageType> findByEvaluationAreaId(Integer evaluationAreaId);

	boolean existByEvaluationAreaId(Integer evaluationAreaId);

	boolean existByName(String name);
}
