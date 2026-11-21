package backend.msclasificator.medicalimage.application.port.out;

import backend.msclasificator.medicalimage.domain.MedicalImageType;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface MedicalImageTypeRepository {

    PageResult<MedicalImageType> findAll(PageQuery query);

    // Coincidencia parcial, sin distinguir mayúsculas/minúsculas
    PageResult<MedicalImageType> searchByName(String query, PageQuery pageQuery);

    Optional<MedicalImageType> findById(Integer id);

    List<MedicalImageType> findByEvaluationAreaId(Integer evaluationAreaId);

    boolean existsById(Integer id);

    boolean existsByName(String name);

    boolean existsByEvaluationAreaId(Integer evaluationAreaId);

    long count();

    MedicalImageType save(MedicalImageType medicalImageType);

    void delete(MedicalImageType medicalImageType);
}
