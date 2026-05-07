package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Doctor findByCode(String code);
    Doctor findByUserId(String userId);
}
