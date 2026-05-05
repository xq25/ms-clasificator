package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
        // Buscar paciente por cédula (documento único de identidad)
        Optional<Patient> findByCc(String cc);

        // Verificar si ya existe un paciente con esa cédula (útil al registrar)
        boolean existsByCc(String cc);
}
