package backend.msclasificator.classification.infrastructure.persistence;

import backend.msclasificator.classification.application.port.out.ClassificationDiagnosisRepository;
import backend.msclasificator.classification.domain.ClassificationDiagnosis;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Las consultas derivadas JPA conservan los nombres de campo de la entidad (imageDiagnostic) — ADR-003
@Component
class ClassificationDiagnosisPersistenceAdapter implements ClassificationDiagnosisRepository {

    private final ClassificationDiagnosisJpaRepository jpa;

    ClassificationDiagnosisPersistenceAdapter(ClassificationDiagnosisJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<ClassificationDiagnosis> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public PageResult<ClassificationDiagnosis> findByImageClassificationId(Integer imageClassificationId, PageQuery query) {
        return PageMapper.toResult(jpa.findByImageDiagnosticId(imageClassificationId, PageMapper.toPageable(query)));
    }

    @Override
    public List<ClassificationDiagnosis> findByImageClassificationId(Integer imageClassificationId) {
        return jpa.findByImageDiagnosticId(imageClassificationId);
    }

    @Override
    public Optional<ClassificationDiagnosis> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public boolean existsByImageClassificationIdAndMedicalDiagnosticId(Integer imageClassificationId, Integer medicalDiagnosticId) {
        return jpa.existsByImageDiagnosticIdAndMedicalDiagnosticId(imageClassificationId, medicalDiagnosticId);
    }

    @Override
    public boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        return jpa.existsByMedicalDiagnosticId(medicalDiagnosticId);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public ClassificationDiagnosis save(ClassificationDiagnosis classificationDiagnosis) {
        return jpa.save(classificationDiagnosis);
    }

    @Override
    public void delete(ClassificationDiagnosis classificationDiagnosis) {
        jpa.delete(classificationDiagnosis);
    }
}
