package backend.msclasificator.medicalimage.infrastructure.persistence;

import backend.msclasificator.medicalimage.application.port.out.MedicalImageRepository;
import backend.msclasificator.medicalimage.domain.MedicalImage;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class MedicalImagePersistenceAdapter implements MedicalImageRepository {

    private final MedicalImageJpaRepository jpa;

    MedicalImagePersistenceAdapter(MedicalImageJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<MedicalImage> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAllByActiveTrue(PageMapper.toPageable(query)));
    }

    @Override
    public Optional<MedicalImage> findById(Integer id) {
        return jpa.findByIdAndActiveTrue(id);
    }

    @Override
    public Optional<MedicalImage> findByIdIncludingRetired(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public PageResult<MedicalImage> findByMedicalImageTypeId(Integer medicalImageTypeId, PageQuery query) {
        return PageMapper.toResult(jpa.findByMedicalImageTypeIdAndActiveTrue(medicalImageTypeId, PageMapper.toPageable(query)));
    }

    @Override
    public List<MedicalImage> findByClinicalRecordIdIncludingRetired(Integer clinicalRecordId) {
        return jpa.findByClinicalRecordId(clinicalRecordId);
    }

    @Override
    public PageResult<MedicalImage> findByClinicalRecordId(Integer clinicalRecordId, PageQuery query) {
        return PageMapper.toResult(jpa.findByClinicalRecordIdAndActiveTrue(clinicalRecordId, PageMapper.toPageable(query)));
    }

    @Override
    public List<MedicalImage> findUndiagnosedImagesByDoctorAndMedicalImageType(Integer doctorId, Integer medicalImageTypeId) {
        return jpa.findUndiagnosedImagesByDoctorAndMedicalImageType(doctorId, medicalImageTypeId);
    }

    @Override
    public boolean existsByMedicalImageTypeId(Integer medicalImageTypeId) {
        return jpa.existsByMedicalImageTypeId(medicalImageTypeId);
    }

    @Override
    public void detachImagesByPatientId(Integer patientId) {
        jpa.detachImagesByPatientId(patientId);
    }

    @Override
    public long count() {
        return jpa.countByActiveTrue();
    }

    @Override
    public MedicalImage save(MedicalImage medicalImage) {
        return jpa.save(medicalImage);
    }

    @Override
    public void delete(MedicalImage medicalImage) {
        jpa.delete(medicalImage);
    }
}
