package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.ImageDoctorDiagnostics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageDoctorDiagnosticsRepository extends JpaRepository<ImageDoctorDiagnostics, Integer> {
    List<ImageDoctorDiagnostics> findByImageDiagnosticId(Integer imageDIagnosticId);

    List<ImageDoctorDiagnostics> findByMedicalDiagnosticId(Integer medicalDiagnosticId);

    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    boolean existsByImageDiagnosticIdAndMedicalDiagnosticId(Integer imageDiagnosticId, Integer medicalDiagnosticId);
}
