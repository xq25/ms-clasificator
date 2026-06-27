package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.ImageDiagnostic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageDiagnosticRepository extends JpaRepository<ImageDiagnostic, Integer> {

    ImageDiagnostic findByDoctorIdAndMedicalImgId(Integer doctorId, Integer medicalImgId);

    List<ImageDiagnostic> findByDoctorId(Integer doctorId);
    Page<ImageDiagnostic> findByDoctorId(Integer doctorId, Pageable pageable);

    List<ImageDiagnostic> findByMedicalImgId(Integer medicalImgId);
    Page<ImageDiagnostic> findByMedicalImgId(Integer medicalImgId, Pageable pageable);

    boolean existsByMedicalImgId(Integer medicalImgId);

    boolean existsByDoctorId(Integer doctorId);

    @Query("SELECT COUNT(i) FROM ImageDiagnostic i")
    long countAll();
}
