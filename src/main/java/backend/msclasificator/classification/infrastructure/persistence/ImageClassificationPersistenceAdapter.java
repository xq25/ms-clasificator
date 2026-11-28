package backend.msclasificator.classification.infrastructure.persistence;

import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.classification.domain.ImageClassification;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Las consultas derivadas JPA conservan los nombres de campo de la entidad (medicalImg) — ADR-003
@Component
class ImageClassificationPersistenceAdapter implements ImageClassificationRepository {

    private final ImageClassificationJpaRepository jpa;

    ImageClassificationPersistenceAdapter(ImageClassificationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<ImageClassification> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public PageResult<ImageClassification> findByDoctorId(Integer doctorId, PageQuery query) {
        return PageMapper.toResult(jpa.findByDoctorId(doctorId, PageMapper.toPageable(query)));
    }

    @Override
    public PageResult<ImageClassification> findByMedicalImageId(Integer medicalImageId, PageQuery query) {
        return PageMapper.toResult(jpa.findByMedicalImgId(medicalImageId, PageMapper.toPageable(query)));
    }

    @Override
    public Optional<ImageClassification> findByDoctorIdAndMedicalImageId(Integer doctorId, Integer medicalImageId) {
        return Optional.ofNullable(jpa.findByDoctorIdAndMedicalImgId(doctorId, medicalImageId));
    }

    @Override
    public Optional<ImageClassification> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByDoctorId(Integer doctorId) {
        return jpa.existsByDoctorId(doctorId);
    }

    @Override
    public long countByMedicalImageId(Integer medicalImageId) {
        return jpa.countByMedicalImgId(medicalImageId);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public ImageClassification save(ImageClassification imageClassification) {
        return jpa.save(imageClassification);
    }

    @Override
    public void delete(ImageClassification imageClassification) {
        jpa.delete(imageClassification);
    }
}
