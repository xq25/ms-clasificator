package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.ImageDoctorDiagnostics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageDoctorDiagnosticsRepository extends JpaRepository<ImageDoctorDiagnostics, Integer> {
    List<ImageDoctorDiagnostics> findByImageDiagnosticId(Integer imageDIagnosticId);
    Page<ImageDoctorDiagnostics> findByImageDiagnosticId(Integer imageDiagnosticId, Pageable pageable);

    List<ImageDoctorDiagnostics> findByMedicalDiagnosticId(Integer medicalDiagnosticId);

    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    boolean existsByImageDiagnosticIdAndMedicalDiagnosticId(Integer imageDiagnosticId, Integer medicalDiagnosticId);

    @Query("SELECT COUNT(i) FROM ImageDoctorDiagnostics i")
    long countAll();
}
