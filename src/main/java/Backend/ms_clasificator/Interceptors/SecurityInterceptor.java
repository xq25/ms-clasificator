package Backend.ms_clasificator.Interceptors;

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

        // Si no trae headers de autorización, lanzamos excepción
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
            // Preparamos los datos de permiso a validar
            Map<String, String> permissionData = new HashMap<>();
            permissionData.put("url", url);
            permissionData.put("method", method);

            // Ruta o endpoint del Permission-Validation
            String validationUrl = securityUrl + "/api/public/security/permissions-validation";

            // Preparamos headers con el token y content-type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            //Agregamos el token al header a la peticion del otro microservicio
            headers.set("Authorization", authHeader);

            // Creamos la entidad de la petición con headers
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(permissionData, headers);

        // Disparador de la peticion al otro ms.
            // Hacemos la petición POST al servicio de seguridad PASANDO HEADERS
            ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                validationUrl,
                HttpMethod.POST,
                requestEntity,
                Boolean.class
            );

            Boolean isAuthorized = responseEntity.getBody();

            if (isAuthorized != null && isAuthorized) {
                logger.info("Acceso autorizado para: {} {}", method, url);
                return true;
            } else {
                logger.warn("Permisos insuficientes para: {} {}", method, url);
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Permisos insuficientes\"}");
                return false;
            }

        } catch (Exception error) {
            logger.error("Error al validar permisos: {}", error.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Error al validar permisos\"}");
            return false;
        }
    }
}

