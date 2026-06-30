package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.MedicalImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MedicalImgRepository extends JpaRepository<MedicalImg, Integer> {

    List<MedicalImg> findByMedicalImageTypeId(Integer medicalImageTypeId);
    Page<MedicalImg> findByMedicalImageTypeId(Integer medicalImageTypeId, Pageable pageable);

    List<MedicalImg> findByClinicalRecordId(Integer clinicalRecordId);
    Page<MedicalImg> findByClinicalRecordId(Integer clinicalRecordId, Pageable pageable);

    // Consulta para la busqueda de las iamgenes con un tipo de imagen especifico que no han sido diagnosticadas por un doctor en especifico
    @Query("""
        SELECT m
        FROM MedicalImg m
        WHERE m.medicalImageType.id = :medicalImageTypeId
          AND NOT EXISTS (
                SELECT d
                FROM ImageDiagnostic d
                WHERE d.medicalImg = m
                  AND d.doctor.id = :doctorId
          )
    """)
    List<MedicalImg> findUndiagnosedImagesByDoctorAndMedicalImageType(@Param("doctorId") Integer doctorId, @Param("medicalImageTypeId") Integer medicalImageTypeId);

    boolean existsByMedicalImageTypeId(Integer medicalImageTypeId);

    @Query("SELECT COUNT(m) FROM MedicalImg m")
    long countAll();

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
        UPDATE MedicalImg m
        SET m.clinicalRecord = null
        WHERE m.clinicalRecord.patient.id = :patientId
    """)
    void detachImagesByPatientId(@Param("patientId") Integer patientId);


}