package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Response.ApiResponse;
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

    // Funcionalidad de comunicacion publicc a con nuestro otro ms para validar existencia de usuarios.
    public boolean existUserById(String userId) {

        try {

            String url = securityUrl + "/api/public/security/" + userId + "/exist";

            ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                    url,
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

    public boolean assignDefaultRole(String userId, String defaultRoleId) {

        try {

            String url = securityUrl +
                    "/api/public/security/user/" +
                    userId +
                    "/default-role/" +
                    defaultRoleId;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
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
}