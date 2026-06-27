package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SecurityServices {

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
    public boolean existUserById(String userId) {

        try {

            String url =
                    securityUrl +
                            "/getway/security/api/" +
                            userId +
                            "/exist";

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
    public boolean assignDefaultRole(String userId, String defaultRoleKey) {

        try {

            String url =
                    securityUrl +
                            "/getway/security/api/" +
                            userId +
                            "/assign-role/" +
                            defaultRoleKey;

            ResponseEntity<ApiResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            buildRequestEntity(),
                            ApiResponse.class
                    );

            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().isSuccess();

        } catch (Exception ex) {
            return false;
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

    public UserInfo getUserInfo(String userId){
        // Obtenemos el nombre del usuario
        String username = this.getUserNameById(userId);
        if (username == null){
            return null;
        }

        String email = this.getUserEmailById(userId);
        if(email == null){
            return null;
        }

        UserInfo userInfo =  new UserInfo();
        userInfo.setName(username);
        userInfo.setEmail(email);

        return userInfo;
    }
}