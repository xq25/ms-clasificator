package backend.msclasificator.identity.application.port.out;

import backend.msclasificator.identity.domain.UserInfo;

// Puerto hacia el proveedor de identidad (hoy ms-security, ADR-012).
// Ante fallo o respuesta incompleta del proveedor, los métodos lanzan SecurityServiceException (→ 503, ADR-009).
public interface IdentityProvider {

    // true/false según el proveedor; un fallo del proveedor NO se confunde con "no existe"
    boolean existsUser(String userId);

    // Lanza SecurityServiceException si el proveedor no confirma la asignación
    void assignDefaultRole(String userId, String roleKey);

    // Nunca devuelve null
    UserInfo getUserInfo(String userId);
}
