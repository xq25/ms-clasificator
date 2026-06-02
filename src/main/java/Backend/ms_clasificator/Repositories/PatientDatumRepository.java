package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.PatientDatum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientDatumRepository extends JpaRepository<PatientDatum, Integer> {
    List<PatientDatum> findByClinicalRecord_Id(Integer clinicalRecordId);

    List<PatientDatum> findByPrimitiveDatum_Id(Integer primitiveDatumId);
}
