package backend.msclasificator.clinicalrecord.application.port.out;

import backend.msclasificator.clinicalrecord.domain.PatientDatum;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface PatientDatumRepository {

    PageResult<PatientDatum> findAll(PageQuery query);

    List<PatientDatum> findByClinicalRecordId(Integer clinicalRecordId);

    List<PatientDatum> findByPrimitiveDatumId(Integer primitiveDatumId);

    Optional<PatientDatum> findById(Integer id);

    long count();

    PatientDatum save(PatientDatum patientDatum);

    void delete(PatientDatum patientDatum);
}
