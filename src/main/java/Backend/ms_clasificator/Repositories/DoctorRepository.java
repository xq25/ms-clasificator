package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, String> {
    boolean existsByUserId(UUID userId);
}
