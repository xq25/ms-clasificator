package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.UIState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UIStateRepository extends JpaRepository<UIState, Integer> {
    List<UIState> findByUiConfig_Id(Integer uiConfigId); //pa traer todos los estados de una config
    List<UIState> findByMedicalDiagnostic_Id(Integer medicalDiagnosticId); //pa buscar estados por diagnóstico directamente
}