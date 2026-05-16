package Backend.ms_clasificator.exceptions;

/**
 * Se lanza cuando el tipo MIME del archivo no es una imagen permitida.
 */
public class InvalidImageTypeException extends RuntimeException {

    private final String receivedContentType;

    public InvalidImageTypeException(String receivedContentType) {
        super("Tipo de imagen no permitido: " + receivedContentType +
                ". Solo se aceptan: image/jpeg, image/png, image/webp, image/dicom");
        this.receivedContentType = receivedContentType;
    }

    public String getReceivedContentType() {
        return receivedContentType;
    }
}