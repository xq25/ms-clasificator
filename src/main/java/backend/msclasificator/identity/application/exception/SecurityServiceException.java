package backend.msclasificator.identity.application.exception;

// ms-security no pudo entregar lo solicitado (caído, error HTTP o respuesta incompleta). Se traduce a 503 (ADR-009).
public class SecurityServiceException extends RuntimeException {

    public SecurityServiceException(String message) {
        super(message);
    }

    public SecurityServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
