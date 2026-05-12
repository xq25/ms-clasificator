package Backend.ms_clasificator.configurations;

import Backend.ms_clasificator.Interceptors.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Este archivo esta destinado a la configuracion de seguridad de esta parte del aplicativo
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registramos el interceptor de seguridad para todas las rutas
        // Puedes excluir rutas específicas si es necesario (ej: login, registro)
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/api/**")
                // Aquí puedes excluir rutas públicas
                .excludePathPatterns(
                        "/api/public/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );
    }
}

