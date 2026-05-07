package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.DoctorArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorAreaRepository extends JpaRepository<DoctorArea, Integer> {

    @Query("SELECT da FROM DoctorArea da WHERE da.doctor.id = :doctorId AND da.evaluationArea.id = :evaluationAreaId")
    DoctorArea findByDoctorIdAndEvaluationAreaId(@Param("doctorId") Integer doctorId, @Param("evaluationAreaId") Integer evaluationAreaId);
}
