package backend.msclasificator.diagnosiscatalog.application.port.out;

import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

// Soft delete (ADR-013): todas las lecturas devuelven solo diagnósticos activos, salvo los métodos "IncludingRetired"
public interface MedicalDiagnosticRepository {

    PageResult<MedicalDiagnostic> findAll(PageQuery query);

    // Coincidencia parcial, sin distinguir mayúsculas/minúsculas
    PageResult<MedicalDiagnostic> searchByName(String query, PageQuery pageQuery);

    // Coincidencia parcial, sin distinguir mayúsculas/minúsculas
    PageResult<MedicalDiagnostic> searchByCode(String query, PageQuery pageQuery);

    Optional<MedicalDiagnostic> findById(Integer id);

    // Incluye retirados: consulta por id (se muestra marcado como retirado) y validaciones con mensaje propio
    Optional<MedicalDiagnostic> findByIdIncludingRetired(Integer id);

    // Incluye retirados: el código CIE-10 nunca se reutiliza (ADR-013)
    Optional<MedicalDiagnostic> findByDiagnosticCodeIncludingRetired(String diagnosticCode);

    List<MedicalDiagnostic> findByParentDiagnosticId(Integer parentDiagnosticId);

    boolean existsById(Integer id);

    // Sub-diagnósticos activos (bloquean el retiro del padre, ADR-013)
    long countActiveChildren(Integer parentDiagnosticId);

    long count();

    MedicalDiagnostic save(MedicalDiagnostic medicalDiagnostic);

    void delete(MedicalDiagnostic medicalDiagnostic);
}
