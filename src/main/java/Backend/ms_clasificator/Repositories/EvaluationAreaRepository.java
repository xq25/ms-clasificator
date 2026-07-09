package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.EvaluationArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluationAreaRepository extends JpaRepository<EvaluationArea, Integer> {
    EvaluationArea findByName(String name);
    EvaluationArea findByCodeArea(String codeArea);

    @Query("""
        SELECT ea
        FROM EvaluationArea ea
        WHERE ea.id NOT IN (
            SELECT da.evaluationArea.id
            FROM DoctorArea da
            WHERE da.doctor.id = :doctorId
        )
    """)
    List<EvaluationArea> findEvaluationAreasNotAssignedToDoctor(
            @Param("doctorId") Integer doctorId
    );

    boolean existsByName(String name);
    boolean existsByCodeArea(String codeArea);

    @Query("SELECT COUNT(ea) FROM EvaluationArea ea")
    long countAll();

    Page<EvaluationArea> findByNameContainingIgnoreCase(String query, Pageable pageable);

    Page<EvaluationArea> findByCodeAreaContainingIgnoreCase(String query, Pageable pageable);
}
