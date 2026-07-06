package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.PatientDatum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PatientDatumRepository extends JpaRepository<PatientDatum, Integer> {
    List<PatientDatum> findByClinicalRecordId(Integer clinicalRecordId);

    List<PatientDatum> findByPrimitiveDatumId(Integer primitiveDatumId);

    @Query("SELECT COUNT(p) FROM PatientDatum p")
    long countAll();
}
