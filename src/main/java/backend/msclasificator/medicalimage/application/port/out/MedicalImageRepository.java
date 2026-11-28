package backend.msclasificator.medicalimage.application.port.out;

import backend.msclasificator.medicalimage.domain.MedicalImage;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

// Soft delete (ADR-013): todas las lecturas devuelven solo imágenes activas, salvo los métodos "IncludingRetired"
public interface MedicalImageRepository {

    PageResult<MedicalImage> findAll(PageQuery query);

    Optional<MedicalImage> findById(Integer id);

    // Incluye retiradas: consulta por id (se muestra marcada como retirada) y validaciones con mensaje propio
    Optional<MedicalImage> findByIdIncludingRetired(Integer id);

    PageResult<MedicalImage> findByMedicalImageTypeId(Integer medicalImageTypeId, PageQuery query);

    // Incluye retiradas: al borrar una historia clínica se desvinculan todas sus imágenes
    List<MedicalImage> findByClinicalRecordIdIncludingRetired(Integer clinicalRecordId);

    PageResult<MedicalImage> findByClinicalRecordId(Integer clinicalRecordId, PageQuery query);

    // Cola del doctor: imágenes activas del tipo indicado que aún no ha clasificado
    List<MedicalImage> findUndiagnosedImagesByDoctorAndMedicalImageType(Integer doctorId, Integer medicalImageTypeId);

    boolean existsByMedicalImageTypeId(Integer medicalImageTypeId);

    // Pone clinical_record = null en todas las imágenes de las historias clínicas del paciente
    void detachImagesByPatientId(Integer patientId);

    long count();

    MedicalImage save(MedicalImage medicalImage);

    void delete(MedicalImage medicalImage);
}
