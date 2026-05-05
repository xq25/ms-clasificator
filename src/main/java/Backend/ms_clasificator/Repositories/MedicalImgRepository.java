package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalImgRepository extends JpaRepository<MedicalImg, Integer> {

    // Todas las imágenes de un paciente
    List<MedicalImg> findByPatient(Patient patient);

    // Todas las imágenes de un paciente por su id
    List<MedicalImg> findByPatientId(Integer patientId);

    // Todas las imágenes de un área de evaluación (ej: todas las de Cardiología)
    List<MedicalImg> findByEvaluationArea(EvaluationArea evaluationArea);

    // Imágenes de un paciente filtradas por área (ej: radiografías de tórax del paciente X)
    List<MedicalImg> findByPatientAndEvaluationArea(Patient patient, EvaluationArea evaluationArea);

}
