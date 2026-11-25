package backend.msclasificator.clinicalrecord.infrastructure.persistence;

import backend.msclasificator.clinicalrecord.domain.PatientDatum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PatientDatumJpaRepository extends JpaRepository<PatientDatum, Integer> {
    List<PatientDatum> findByClinicalRecordId(Integer clinicalRecordId);

    List<PatientDatum> findByPrimitiveDatumId(Integer primitiveDatumId);

    long countByPrimitiveDatumId(Integer primitiveDatumId);

    @Query("SELECT COUNT(p) FROM PatientDatum p")
    long countAll();
}
