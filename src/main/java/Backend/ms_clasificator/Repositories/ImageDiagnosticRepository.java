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
    ImageDiagnostic findByDoctor_IdAndMedicalImg_Id(Integer doctorId, Integer medicalImgId);

    List<ImageDiagnostic> findByDoctor_Id(Integer doctorId);

    List<ImageDiagnostic> findByMedicalImg_Id(Integer medicalImgId);

    List<ImageDiagnostic> findByMedicalDiagnostic_Id(Integer medicalDiagnosticId);
}
