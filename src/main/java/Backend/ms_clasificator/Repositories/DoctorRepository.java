package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
     //buscar doctor por su codigo por consultas varias
     Optional<Doctor> findById(String code);

     //buscar doctor por su id de usuario para consultas varias
     Optional<Doctor> findByUser_id(UUID user_id);

     // Verificar si existe un doctor con ese user_id (útil al eliminar usuario del ms-security)
     boolean existsByUser_id(UUID userId);
}
