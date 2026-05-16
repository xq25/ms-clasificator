package Backend.ms_clasificator.exceptions;

/**
 * Excepción base para errores de almacenamiento de imágenes.
 * Runtime para no obligar a los services a hacer try/catch en todos lados.
 */
public class ImageStorageException extends RuntimeException {

    private final String provider;

    public ImageStorageException(String message, String provider) {
        super(message);
        this.provider = provider;
    }

    public ImageStorageException(String message, String provider, Throwable cause) {
        super(message, cause);
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }
}