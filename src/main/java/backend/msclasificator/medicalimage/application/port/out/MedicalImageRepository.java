package backend.msclasificator.medicalimage.application.port.out;

import backend.msclasificator.medicalimage.domain.MedicalImage;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface MedicalImageRepository {

    PageResult<MedicalImage> findAll(PageQuery query);

    Optional<MedicalImage> findById(Integer id);

    PageResult<MedicalImage> findByMedicalImageTypeId(Integer medicalImageTypeId, PageQuery query);

    List<MedicalImage> findByClinicalRecordId(Integer clinicalRecordId);

    PageResult<MedicalImage> findByClinicalRecordId(Integer clinicalRecordId, PageQuery query);

    // Imágenes del tipo indicado que el doctor aún no ha clasificado
    List<MedicalImage> findUndiagnosedImagesByDoctorAndMedicalImageType(Integer doctorId, Integer medicalImageTypeId);

    boolean existsByMedicalImageTypeId(Integer medicalImageTypeId);

    // Pone clinical_record = null en todas las imágenes de las historias clínicas del paciente
    void detachImagesByPatientId(Integer patientId);

    long count();

    MedicalImage save(MedicalImage medicalImage);

    void delete(MedicalImage medicalImage);
}
