package backend.msclasificator.diagnosiscatalog.application.port.out;

import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface MedicalDiagnosticRepository {

    PageResult<MedicalDiagnostic> findAll(PageQuery query);

    // Coincidencia parcial, sin distinguir mayúsculas/minúsculas
    PageResult<MedicalDiagnostic> searchByName(String query, PageQuery pageQuery);

    // Coincidencia parcial, sin distinguir mayúsculas/minúsculas
    PageResult<MedicalDiagnostic> searchByCode(String query, PageQuery pageQuery);

    Optional<MedicalDiagnostic> findById(Integer id);

    Optional<MedicalDiagnostic> findByDiagnosticCode(String diagnosticCode);

    List<MedicalDiagnostic> findByParentDiagnosticId(Integer parentDiagnosticId);

    boolean existsById(Integer id);

    boolean existsByParentDiagnosticId(Integer parentDiagnosticId);

    long count();

    MedicalDiagnostic save(MedicalDiagnostic medicalDiagnostic);

    void delete(MedicalDiagnostic medicalDiagnostic);
}
