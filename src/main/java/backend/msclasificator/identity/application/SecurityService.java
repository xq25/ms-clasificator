package backend.msclasificator.identity.application;

import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.identity.application.exception.SecurityServiceException;
import backend.msclasificator.identity.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class SecurityService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.security.url}")
    private String securityUrl;

    @Value("${ms.secret.key}")
    private String secretKey;


    // HEADERS CONFIG
    private HttpEntity<Void> buildRequestEntity() {

        HttpHeaders headers = new HttpHeaders();
        headers.set("secretKey", secretKey);

        return new HttpEntity<>(headers);
    }

    // EXIST USER BY ID
    // true/false según ms-security (404 = no existe). Si ms-security falla o responde algo inválido
    // lanza SecurityServiceException (503, ADR-009) para no confundir la caída con "el usuario no existe".
    public boolean existUserById(String userId) {
        try {
            String url = securityUrl + "/getway/security/api/" + userId + "/exist";
            ResponseEntity<ApiResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, buildRequestEntity(), ApiResponse.class);

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

    // EXIST USER BY EMAIL
    public boolean existUserByEmail(String email) {

        try {

            String url =
                    securityUrl +
                            "/getway/security/api/user-exist/email/" +
                            email;

            ResponseEntity<ApiResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            buildRequestEntity(),
                            ApiResponse.class
                    );

            if (response.getBody() != null) {

                Object data = response.getBody().getData();

                if (data instanceof Boolean) {
                    return (Boolean) data;
                }
            }

            return false;

        } catch (Exception ex) {
            return false;
        }
    }

    // ASSIGN DEFAULT ROLE
    /**Nosotros desde logica no conocemos los ids de los roles por defecto, nada mas las entidades que nos corresponden
     * Por eso solo enviamos el key o name del role que queremos asignar, y el getway se encarga de resolverlo y asignarlo,
     * si es que es un role permitido.
     */
    // Lanza SecurityServiceException si ms-security no confirma la asignación (ADR-009):
    // el alta que la invoca es transaccional y hace rollback (bug #9).
    public void assignDefaultRole(String userId, String defaultRoleKey) {
        try {
            String url = securityUrl + "/getway/security/api/" + userId + "/assign-role/" + defaultRoleKey;
            ResponseEntity<ApiResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, buildRequestEntity(), ApiResponse.class);

            boolean asignado = response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().isSuccess();
            if (!asignado) {
                throw new SecurityServiceException("ms-security no asignó el rol '" + defaultRoleKey + "' al usuario");
            }
        } catch (RestClientException ex) {
            throw new SecurityServiceException("No se pudo asignar el rol '" + defaultRoleKey + "' en ms-security", ex);
        }
    }

    public String getUserNameById(String userId) {
        String url =
                securityUrl +
                        "/getway/security/api/get-name/"+ userId;

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        buildRequestEntity(),
                            String.class
                );

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;


    }

    public String getUserEmailById(String userId){

        String url =
                securityUrl +
                        "/getway/security/api/get-email/"+ userId;

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        buildRequestEntity(),
                        String.class
                );

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;

    }

    // Nunca devuelve null: si ms-security falla o responde incompleto lanza SecurityServiceException (→ 503, ADR-009)
    public UserInfo getUserInfo(String userId) {
        String mensaje = "No se pudo obtener la información del usuario (nombre y email) desde ms-security";
        String username;
        String email;
        try {
            username = this.getUserNameById(userId);
            email = username == null ? null : this.getUserEmailById(userId);
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
}