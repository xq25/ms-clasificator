package backend.msclasificator.classification.infrastructure.persistence;

import backend.msclasificator.classification.domain.ImageClassification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageClassificationJpaRepository extends JpaRepository<ImageClassification, Integer> {

    ImageClassification findByDoctorIdAndMedicalImgId(Integer doctorId, Integer medicalImgId);

    List<ImageClassification> findByDoctorId(Integer doctorId);
    Page<ImageClassification> findByDoctorId(Integer doctorId, Pageable pageable);

    List<ImageClassification> findByMedicalImgId(Integer medicalImgId);
    Page<ImageClassification> findByMedicalImgId(Integer medicalImgId, Pageable pageable);

    boolean existsByMedicalImgId(Integer medicalImgId);

    boolean existsByDoctorId(Integer doctorId);

    boolean existsByDatasetCategoryId(Integer datasetCategoryId);

    boolean existsByDatasetCategoryDatasetId(Integer datasetId);

    @Query("SELECT COUNT(i) FROM ImageClassification i")
    long countAll();
}
