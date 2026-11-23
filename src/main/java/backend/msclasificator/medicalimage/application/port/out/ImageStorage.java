package backend.msclasificator.medicalimage.application.port.out;

// Puerto hacia el almacenamiento de imágenes (MinIO hoy; S3/R2 preparado, ADR-012).
// No valida tipo ni tamaño: eso lo hace MedicalImageService antes de subir.
// Ante fallo del proveedor, los métodos lanzan ImageStorageException.
public interface ImageStorage {

    // Devuelve el imageKey generado ({folder}/{uuid}.{extensión})
    String upload(ImageFile file, String folder);

    String generatePublicUrl(String imageKey);

    void delete(String imageKey);

    boolean exists(String imageKey);

    String providerName();
}
