package backend.msclasificator.identity.infrastructure.http;

import backend.msclasificator.identity.application.exception.SecurityServiceException;
import backend.msclasificator.identity.application.port.out.IdentityProvider;
import backend.msclasificator.identity.domain.UserInfo;
import backend.msclasificator.shared.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

// Adaptador HTTP del puerto IdentityProvider hacia ms-security (ADR-012).
// Las llamadas internas se autentican con el header secretKey; los fallos se traducen a SecurityServiceException (ADR-009).
@Component
class MsSecurityIdentityProvider implements IdentityProvider {

    private static final String API = "/getway/security/api/";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.security.url}")
    private String securityUrl;

    @Value("${ms.secret.key}")
    private String secretKey;

    // 404 o data=false: no existe. Caída o respuesta inválida: excepción, para no confundirla con "no existe".
    @Override
    public boolean existsUser(String userId) {
        try {
            ResponseEntity<ApiResponse> response =
                    restTemplate.exchange(securityUrl + API + userId + "/exist", HttpMethod.GET, buildRequestEntity(), ApiResponse.class);

            Object data = response.getBody() != null ? response.getBody().getData() : null;
            if (data instanceof Boolean exists) {
                return exists;
            }
            throw new SecurityServiceException("Respuesta inválida de ms-security al verificar el usuario");
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (RestClientException ex) {
            throw new SecurityServiceException("No se pudo verificar el usuario en ms-security", ex);
        }
    }

    // ms-security resuelve el rol por su clave (patient/doctor); este servicio no conoce los ids de los roles.
    // Las altas que lo invocan son transaccionales y hacen rollback si falla (bug #9).
    @Override
    public void assignDefaultRole(String userId, String roleKey) {
        try {
            ResponseEntity<ApiResponse> response =
                    restTemplate.exchange(securityUrl + API + userId + "/assign-role/" + roleKey, HttpMethod.POST, buildRequestEntity(), ApiResponse.class);

            boolean asignado = response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().isSuccess();
            if (!asignado) {
                throw new SecurityServiceException("ms-security no asignó el rol '" + roleKey + "' al usuario");
            }
        } catch (RestClientException ex) {
            throw new SecurityServiceException("No se pudo asignar el rol '" + roleKey + "' en ms-security", ex);
        }
    }

    @Override
    public UserInfo getUserInfo(String userId) {
        String mensaje = "No se pudo obtener la información del usuario (nombre y email) desde ms-security";
        String username;
        String email;
        try {
            username = getString(API + "get-name/" + userId);
            email = username == null ? null : getString(API + "get-email/" + userId);
        } catch (RestClientException ex) {
            throw new SecurityServiceException(mensaje, ex);
        }
        if (username == null || email == null) {
            throw new SecurityServiceException(mensaje);
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setName(username);
        userInfo.setEmail(email);
        return userInfo;
    }

    private String getString(String path) {
        return restTemplate.exchange(securityUrl + path, HttpMethod.GET, buildRequestEntity(), String.class).getBody();
    }

    private HttpEntity<Void> buildRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("secretKey", secretKey);
        return new HttpEntity<>(headers);
    }
}
