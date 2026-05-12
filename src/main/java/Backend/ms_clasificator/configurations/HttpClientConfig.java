package Backend.ms_clasificator.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
// Este archivo esta destinado a poder generar las peticiones entre microservicios. Lo que permite realizar validaciones o peticiones con normalidad a otras partes del aplicativo
public class HttpClientConfig {

    /**
     * Crea un bean de RestTemplate para hacer peticiones HTTP
     * Esta configuración está separada para evitar dependencias circulares
     * @return RestTemplate bean
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

