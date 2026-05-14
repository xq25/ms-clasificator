package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.UIConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UIConfigRepository extends JpaRepository<UIConfig, Integer> {
    List<UIConfig> findByMedicalDiagnostic_Id(Integer medicalDiagnosticId); //pa buscar todas las configs asociadas a un diagnóstico
    boolean existsByMedicalDiagnostic_Id(Integer medicalDiagnosticId);  // pa validar antes de crear duplicados
}