package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.MedicalImageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalImageTypeRepository extends JpaRepository<MedicalImageType, Integer> {

	MedicalImageType findByName(String name);
}
