package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad MedicalImg — versión actualizada con soporte de storage desacoplado.

 * CAMPOS NUEVOS vs versión original:
 * - image_key   → clave única del objeto en el storage (diagnostics/uuid.jpg)
 * - provider    → qué sistema la guardó ("minio", "s3", "r2") — útil para auditoría
 *                 y para saber a dónde ir a buscarla si necesitas migrar datos
 * - content_type → MIME type del archivo original (image/jpeg, etc.)
 * - file_size   → tamaño en bytes — útil para billing y validaciones

 * CAMPO ELIMINADO:
 * - url → ya NO se guarda en la BD. La URL se genera dinámicamente con
 *          ImageStoragePort.generatePublicUrl(imageKey).
 *   Por qué: las URLs pre-signed expiran. Si guardas la URL, en 24h está muerta.
 *   El image_key siempre es válido — la URL se genera on-demand.

 * DECISIÓN — created_at aquí vs en la entidad de storage:
 * Mantenemos created_at en esta entidad porque ya existe en el dominio médico.
 * No tiene sentido una entidad separada solo para el registro de storage
 * si el modelo ya existe.
 */
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medical_img")
public class MedicalImg extends SystemDatum{

    /**
     * Clave única del objeto en el storage.
     * Ejemplo: "diagnostics/3f8a1b2c-4d5e-6f7a-8b9c.jpg"
     * Este es el valor con el que se consulta/elimina la imagen.
     */
    @Column(name = "image_key", nullable = false, unique = true)
    private String imageKey;

    /**
     * Provider que almacena esta imagen.
     * Permite saber a dónde ir a buscarla. Útil en migración.
     * Valores posibles: "minio", "s3", "r2"
     */
    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    /**
     * MIME type del archivo.
     * Útil para servir el Content-Type correcto al cliente.
     */
    @Column(name = "content_type", length = 50)
    private String contentType;

    /**
     * Tamaño del archivo en bytes.
     */
    @Column(name = "file_size")
    private Long fileSize;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Cargamos el tipo imagen medica
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_image_type_id", nullable = false)
    private MedicalImageType medicalImageType;

    // Las imagenes puedenn vivir sin la asociacion con las historia medicas (composicion pasiva)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "clinical_record_id", nullable = true )
    private ClinicalRecord clinicalRecord;



}