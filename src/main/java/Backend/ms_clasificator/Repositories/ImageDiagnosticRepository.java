package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.ImageDiagnostic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageDiagnosticRepository extends JpaRepository<ImageDiagnostic, Integer> {

    /**
     * Buscar un registro de ImageDiagnostic por doctor_id y medical_img_id
     * @param doctorId ID del doctor
     * @param medicalImgId ID de la imagen médica
     * @return ImageDiagnostic encontrado o null
     */
    ImageDiagnostic findByDoctorIdAndMedicalImgId(Integer doctorId, Integer medicalImgId);

    List<ImageDiagnostic> findByDoctorId(Integer doctorId);

    List<ImageDiagnostic> findByMedicalImgId(Integer medicalImgId);

    boolean existsByMedicalImgId(Integer medicalImgId);

    boolean existsByDoctorId(Integer doctorId);

}
