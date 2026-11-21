package backend.msclasificator.doctor.infrastructure.persistence;

import backend.msclasificator.doctor.domain.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorJpaRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByCode(String code);
    Optional<Doctor> findByUserId(String userId);

    @Query("""
        SELECT d
        FROM Doctor d
        WHERE NOT EXISTS (
            SELECT da
            FROM DoctorArea da
            WHERE da.doctor.id = d.id
            AND da.evaluationArea.id = :evaluationAreaId
        )
    """)
    List<Doctor> findDoctorsNotAssignedToEvaluationArea(@Param("evaluationAreaId") Integer evaluationAreaId);

    boolean existsByCode(String code);
    boolean existsByUserId(String userId);

    @Query("SELECT COUNT(d) FROM Doctor d")
    long countAll();

    Page<Doctor> findByCodeContainingIgnoreCase(String query, Pageable pageable);
}
