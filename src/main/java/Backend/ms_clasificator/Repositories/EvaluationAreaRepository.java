package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.EvaluationArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationAreaRepository extends JpaRepository<EvaluationArea, Integer> {
        EvaluationArea findByDoctorAreaId(Integer doctorAreaId);

        //buscar area por su codigo unico
        Optional<EvaluationArea> findByCodeArea(String codeArea);
        //BUSCAR POR nombre de área
        Optional<EvaluationArea> findByName(String name);

        // verificar si ya existe un aarea con el nombre
        boolean existByName(String name);
}
