package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.MedicalImg;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalImgRepository extends JpaRepository<MedicalImg, Integer> {


    /** Buscar todas las imágenes médicas por medical_image_type_id
     * @param medicalImageTypeId ID del tipo de imagen médica
     * @return Lista de imágenes médicas del tipo
     * */
    List<MedicalImg> findByMedicalImageTypeId(Integer medicalImageTypeId);

    List<MedicalImg> findByClinicalRecordId(Integer clinicalRecordId);


}
