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

    /** Buscar todas las imágenes médicas por evaluation_area_id
     * @param evaluationAreaId ID del área de evaluación
     * @return Lista de imágenes médicas de la área
     * */
    List<MedicalImg> findByEvaluationAreaId(Integer evaluationAreaId);

}
