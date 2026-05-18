package Backend.ms_clasificator.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SecurityServices {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.security.url}")
    private String securityUrl;

    /**
     * Validar si un usuario existe en el MS Security
     */
    public boolean existUserById(String userId) {

        try {

            String url = securityUrl + "/api/public/security/" + userId + "/exist";

            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    url,
                    Boolean.class
            );

            return Boolean.TRUE.equals(response.getBody());

        } catch (Exception ex) {
            return false;
        }
    }
}