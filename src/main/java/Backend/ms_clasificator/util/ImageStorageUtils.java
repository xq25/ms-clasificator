package Backend.ms_clasificator.util;

import Backend.ms_clasificator.exceptions.InvalidImageTypeException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

/**
 * Utilidades de storage. Clase estática — no es un bean de Spring.
 * No tiene estado, no necesita inyección.
 */
public final class ImageStorageUtils {

    // Tipos MIME permitidos para imágenes médicas y etiquetas
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/tiff",   // común en diagnósticos
            "application/dicom" // DICOM médico
    );

    private ImageStorageUtils() {}

    /**
     * Valida que el archivo sea una imagen permitida.
     * Lanza InvalidImageTypeException si no lo es.
     */
    public static void validateImageType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new InvalidImageTypeException(contentType != null ? contentType : "null");
        }
    }

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
    public static String generateImageKey(String originalFilename, String folder) {
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

    /**
     * Valida tamaño máximo de imagen (10 MB para médicas).
     */
    public static void validateImageSize(MultipartFile file, long maxSizeBytes) {
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException(
                    "La imagen excede el tamaño máximo permitido de " + (maxSizeBytes / 1024 / 1024) + " MB"
            );
        }
    }
}