package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.MedicalImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<MedicalImg, Integer> {

}
