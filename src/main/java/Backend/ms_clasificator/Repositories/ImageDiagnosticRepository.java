package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.*;
import org.springframework.data.jpa.repository.JpaRepository
        ;

import java.util.List;

public interface ImageDiagnosticRepository extends JpaRepository<ImageDiagnostic, Integer> {
     // Todas las imágenes de un paciente
     List<MedicalImg> findByPatient(Patient patient);

     // Todas las imágenes de un paciente por su id
     List<MedicalImg> findByPatientId(Integer patientId);

     // Todas las imágenes de un área de evaluación (ej: todas las de Cardiología)
     List<MedicalImg> findByEvaluationArea(EvaluationArea evaluationArea);

     // Imágenes de un paciente filtradas por área (ej: radiografías de tórax del paciente X)
     List<MedicalImg> findByPatientAndEvaluationArea(Patient patient, EvaluationArea evaluationArea);

     //imagens que ha diagnosticado un doctor x
     List<ImageDiagnostic> findByDoctor(Doctor doctor);

     // List<ImageDiagnostic> findByDoctorDoctorCode(String doctorCode); esta no se si funcione

     //diagnosticos realizados a una imagen x
     List<ImageDiagnostic> findByMedicalImgId(Integer imageId);

     //diagnosticos a un paciente x
     List<ImageDiagnostic> findByMedicalImgPatientId(Integer patientId);

     //verificar si un doctor ya reviso una imagen
     boolean existsByDoctorAndMedicalImg(Doctor doctor, MedicalImg medicalImg);
}
