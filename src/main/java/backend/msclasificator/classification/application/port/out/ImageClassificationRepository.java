package backend.msclasificator.classification.application.port.out;

import backend.msclasificator.classification.domain.ImageClassification;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.Optional;

public interface ImageClassificationRepository {

    PageResult<ImageClassification> findAll(PageQuery query);

    PageResult<ImageClassification> findByDoctorId(Integer doctorId, PageQuery query);

    PageResult<ImageClassification> findByMedicalImageId(Integer medicalImageId, PageQuery query);

    // Clasificación que el doctor ya hizo sobre la imagen (única por par doctor–imagen)
    Optional<ImageClassification> findByDoctorIdAndMedicalImageId(Integer doctorId, Integer medicalImageId);

    Optional<ImageClassification> findById(Integer id);

    boolean existsById(Integer id);

    boolean existsByDoctorId(Integer doctorId);

    boolean existsByMedicalImageId(Integer medicalImageId);

    long count();

    ImageClassification save(ImageClassification imageClassification);

    void delete(ImageClassification imageClassification);
}
