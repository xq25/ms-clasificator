package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.MedicalImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MedicalImgRepository extends JpaRepository<MedicalImg, Integer> {


    /** Buscar todas las imágenes médicas por medical_image_type_id
     * @param medicalImageTypeId ID del tipo de imagen médica
     * @return Lista de imágenes médicas del tipo
     * */
    List<MedicalImg> findByMedicalImageTypeId(Integer medicalImageTypeId);

    List<MedicalImg> findByClinicalRecordId(Integer clinicalRecordId);

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