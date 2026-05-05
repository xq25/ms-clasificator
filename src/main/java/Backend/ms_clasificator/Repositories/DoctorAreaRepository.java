package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.DoctorArea;
import Backend.ms_clasificator.Models.EvaluationArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorAreaRepository extends JpaRepository<DoctorArea, Integer> {
     // Todas las áreas asignadas a un doctor
     List<DoctorArea> findByDoctor(Doctor doctor);

     // Todos los doctores asignados a un área
     List<DoctorArea> findByEvaluationArea(EvaluationArea evaluationArea);

     // Verificar si ya existe la asignación doctor-área (evitar duplicados)
     boolean existsByDoctorAndEvaluationArea(Doctor doctor, EvaluationArea evaluationArea);

     // Eliminar todas las asignaciones de un doctor (útil al eliminar el doctor)
     void deleteByDoctor(Doctor doctor);
}
