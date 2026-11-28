package backend.msclasificator.medicalimage.application.port.out;

import backend.msclasificator.medicalimage.domain.MedicalImageType;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

// Soft delete (ADR-013): todas las lecturas devuelven solo tipos activos, salvo los métodos "IncludingRetired"
public interface MedicalImageTypeRepository {

    PageResult<MedicalImageType> findAll(PageQuery query);

    // Coincidencia parcial, sin distinguir mayúsculas/minúsculas
    PageResult<MedicalImageType> searchByName(String query, PageQuery pageQuery);

    Optional<MedicalImageType> findById(Integer id);

    // Incluye retirados: consulta por id (se muestra marcado como retirado) y validaciones con mensaje propio
    Optional<MedicalImageType> findByIdIncludingRetired(Integer id);

    List<MedicalImageType> findByEvaluationAreaId(Integer evaluationAreaId);

    boolean existsById(Integer id);

    // Incluye retirados: las imágenes de un tipo retirado siguen visibles (ADR-013)
    boolean existsByIdIncludingRetired(Integer id);

    boolean existsByName(String name);

    // Nombre en uso por otro tipo de imagen (bug #22)
    boolean existsByNameAndIdNot(String name, Integer id);

    boolean existsByEvaluationAreaId(Integer evaluationAreaId);

    long count();

    MedicalImageType save(MedicalImageType medicalImageType);

    void delete(MedicalImageType medicalImageType);
}
