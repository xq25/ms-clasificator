package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.PatientDatum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientDatumRepository extends JpaRepository<PatientDatum, Integer> {

}
