package backend.msclasificator.medicalimage.application.port.out;

import java.io.InputStream;

// Archivo de imagen independiente de la capa web (ADR-012): el controlador lo crea a partir del MultipartFile.
// Quien abre el stream (el controlador) es quien lo cierra; el storage solo lo lee.
public record ImageFile(String originalFilename, String contentType, long size, InputStream content) {

    public boolean isEmpty() {
        return size == 0;
    }
}
