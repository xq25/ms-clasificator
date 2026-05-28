package Backend.ms_clasificator.Interceptors;

import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.security.url}")
    private String securityUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Extraemos los valores de la petición (Front)
        String url = request.getRequestURI();
        String method = request.getMethod();
        String authHeader = request.getHeader("Authorization");

        // Si NO trae headers de autorización, lanzamos excepción
        if (authHeader == null || authHeader.isEmpty()) {
            logger.warn("Token de autorización faltante para: {} {}", method, url);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Token de autorización faltante\"}");
            return false;
        }

        // Extraemos el token del header (formato: "Bearer <token>")
        String token = authHeader.replace("Bearer ", "");

        try {

            // DATOS DE VALIDACIÓN
            Map<String, String> permissionData = new HashMap<>();
            permissionData.put("url", url);
            permissionData.put("method", method);

            // URL PARA VALIDACION
            String validationUrl = securityUrl + "/api/public/security/permissions-validation";

            // HEADERS
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authHeader); // reenviamos JWT al MS Security

            // ENTITY
            HttpEntity<Map<String, String>> requestEntity =
                    new HttpEntity<>(permissionData, headers);

            // REQUEST AL MS SECURITY
            ResponseEntity<ApiResponse> responseEntity =
                    restTemplate.exchange(
                            validationUrl,
                            HttpMethod.POST,
                            requestEntity,
                            ApiResponse.class
                    );

            // RESPONSE BODY
            ApiResponse apiResponse = responseEntity.getBody();

            if (apiResponse == null) {

                logger.error("Respuesta vacía del MS Security");

                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setContentType("application/json");

                response.getWriter().write(
                        "{\"message\": \"Respuesta inválida del servicio de seguridad\"}"
                );

                return false;
            }

            // EXTRAER DATA
            Boolean isAuthorized =
                    (Boolean) apiResponse.getData();

            // ACCESS GRANTED ( Acceso concedido)
            if (Boolean.TRUE.equals(isAuthorized)) {

                logger.info("Acceso autorizado para: {} {}", method, url);
                return true;
            }

            // ACCESS DENIED ( Token Invalido o Permisos insuficientes )
            logger.warn("{} para: {} {}", apiResponse.getMessage(), method, url);

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");

            response.getWriter().write("{\"message\": \"" + apiResponse.getMessage() + "\"}");
            return false;

        } catch (Exception error) {

            logger.error("Error al validar permisos: {}", error.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            response.getWriter().write("{\"message\": \"Error al validar permisos\"}");
            return false;

        }
    }
}

