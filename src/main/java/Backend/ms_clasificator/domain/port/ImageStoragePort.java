package Backend.ms_clasificator.domain.port;

import org.springframework.web.multipart.MultipartFile;

/**
 * Puerto de salida (Output Port) para almacenamiento de imágenes.
 *
 * DECISIÓN ARQUITECTÓNICA:
 * Esto es un puerto en términos de Arquitectura Hexagonal.
 * El dominio define QUÉ necesita (contrato), sin saber CÓMO se implementa.
 * MinIO, S3, R2, etc. son detalles de infraestructura — el dominio no los conoce.
 *
 * Beneficio: puedes cambiar de MinIO a S3 cambiando un bean en la config.
 * La lógica de negocio (MedicalImageService) no toca ni una línea.
 */
public interface ImageStoragePort {

    /**
     * Sube una imagen al storage y retorna la referencia (image_key).
     *
     * @param file       archivo recibido vía multipart
     * @param folder     carpeta/prefijo lógico (ej: "diagnostics", "labels")
     * @return           image_key único que identifica el objeto en el storage
     */
    String uploadImage(MultipartFile file, String folder);

    /**
     * Genera una URL pública (o pre-signed) para acceder a la imagen.
     *
     * @param imageKey   clave del objeto en el storage (retornada por uploadImage)
     * @return           URL accesible para el cliente
     */
    String generatePublicUrl(String imageKey);

    /**
     * Elimina una imagen del storage.
     *
     * @param imageKey   clave del objeto a eliminar
     */
    void deleteImage(String imageKey);

    /**
     * Verifica si un objeto existe en el storage.
     *
     * @param imageKey   clave del objeto
     * @return           true si existe
     */
    boolean imageExists(String imageKey);

    /**
     * Retorna el nombre del provider actual (útil para logging y auditoría).
     * Ej: "minio", "s3", "r2"
     */
    String getProviderName();
}