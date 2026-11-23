package backend.msclasificator.medicalimage.infrastructure.storage;

import java.util.UUID;

/**
 * Utilidades de storage. Clase estática — no es un bean de Spring.
 * No tiene estado, no necesita inyección.
 * La validación de tipo y tamaño vive en MedicalImageService (ADR-012).
 */
final class ImageStorageUtils {

    private ImageStorageUtils() {}

    /**
     * Genera un nombre único para el objeto en el storage.
     *
     * ESTRATEGIA DE NAMING:
     * {folder}/{uuid}.{extension}
     * Ejemplo: diagnostics/3f8a1b2c-4d5e-6f7a-8b9c-0d1e2f3a4b5c.jpg
     *
     * - UUID evita colisiones sin importar cuántas imágenes suban en paralelo.
     * - El folder permite organizar por contexto (diagnostics, labels, etc).
     * - No se usa el nombre original del archivo: previene path traversal
     *   y evita problemas con caracteres especiales o nombres en blanco.
     *
     * @param originalFilename nombre original del archivo
     * @param folder           carpeta lógica (ej: "diagnostics")
     * @return                 image_key único
     */
    static String generateImageKey(String originalFilename, String folder) {
        String extension = extractExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        String sanitizedFolder = folder.replaceAll("[^a-zA-Z0-9/_-]", "").toLowerCase();
        return sanitizedFolder + "/" + uuid + "." + extension;
    }

    /**
     * Extrae la extensión de un filename. Retorna "jpg" como fallback.
     */
    private static String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
