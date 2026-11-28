package backend.msclasificator.medicalimage.infrastructure.persistence;

import backend.msclasificator.medicalimage.domain.MedicalImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MedicalImageJpaRepository extends JpaRepository<MedicalImage, Integer> {

    // Soft delete (ADR-013): las lecturas del adaptador son solo de activas, salvo las "IncludingRetired"
    Page<MedicalImage> findAllByActiveTrue(Pageable pageable);

    Optional<MedicalImage> findByIdAndActiveTrue(Integer id);

    Page<MedicalImage> findByMedicalImageTypeIdAndActiveTrue(Integer medicalImageTypeId, Pageable pageable);

    // Incluye retiradas: al borrar una historia clínica se desvinculan todas sus imágenes
    List<MedicalImage> findByClinicalRecordId(Integer clinicalRecordId);

    Page<MedicalImage> findByClinicalRecordIdAndActiveTrue(Integer clinicalRecordId, Pageable pageable);

    long countByActiveTrue();

    // Consulta para la busqueda de las iamgenes con un tipo de imagen especifico que no han sido diagnosticadas por un doctor en especifico
    @Query("""
        SELECT m
        FROM MedicalImage m
        WHERE m.medicalImageType.id = :medicalImageTypeId
          AND m.active = true
          AND NOT EXISTS (
                SELECT d
                FROM ImageClassification d
                WHERE d.medicalImg = m
                  AND d.doctor.id = :doctorId
          )
    """)
    List<MedicalImage> findUndiagnosedImagesByDoctorAndMedicalImageType(@Param("doctorId") Integer doctorId, @Param("medicalImageTypeId") Integer medicalImageTypeId);

    boolean existsByMedicalImageTypeId(Integer medicalImageTypeId);


    /**
     * Desvincular en bloque todas las imágenes médicas asociadas a los ClinicalRecord
     * de un paciente específico, poniendo clinical_record_id = NULL.
     * Se debe ejecutar ANTES de eliminar el Patient para evitar violaciones de FK.
     *
     * @param patientId ID del paciente cuyos registros clínicos se van a eliminar
     */
    @Modifying
    @Transactional
    @Query("""
        UPDATE MedicalImage m
        SET m.clinicalRecord = null
        WHERE m.clinicalRecord.patient.id = :patientId
    """)
    void detachImagesByPatientId(@Param("patientId") Integer patientId);


}