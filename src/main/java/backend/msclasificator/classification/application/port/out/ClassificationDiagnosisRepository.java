package backend.msclasificator.classification.application.port.out;

import backend.msclasificator.classification.domain.ClassificationDiagnosis;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface ClassificationDiagnosisRepository {

    PageResult<ClassificationDiagnosis> findAll(PageQuery query);

    PageResult<ClassificationDiagnosis> findByImageClassificationId(Integer imageClassificationId, PageQuery query);

    Optional<ClassificationDiagnosis> findById(Integer id);

    List<ClassificationDiagnosis> findByImageClassificationId(Integer imageClassificationId);

    boolean existsByImageClassificationIdAndMedicalDiagnosticId(Integer imageClassificationId, Integer medicalDiagnosticId);

    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    long count();

    ClassificationDiagnosis save(ClassificationDiagnosis classificationDiagnosis);

    void delete(ClassificationDiagnosis classificationDiagnosis);
}
