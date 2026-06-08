package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByCode(String code);
    Optional<Doctor> findByUserId(String userId);

    boolean existsByCode(String code);
    boolean existsByUserId(String userId);

}
