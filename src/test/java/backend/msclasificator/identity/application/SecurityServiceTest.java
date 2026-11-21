package backend.msclasificator.identity.application;

import backend.msclasificator.identity.application.exception.SecurityServiceException;
import backend.msclasificator.identity.domain.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    private static final String MENSAJE = "No se pudo obtener la información del usuario (nombre y email) desde ms-security";

    @Mock private RestTemplate restTemplate;

    @InjectMocks private SecurityService service;

    private void respuestaNombre(String body) {
        when(restTemplate.exchange(contains("/get-name/u1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(body));
    }

    private void respuestaEmail(String body) {
        when(restTemplate.exchange(contains("/get-email/u1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(body));
    }

    @Nested
    @DisplayName("getUserInfo")
    class GetUserInfo {

        @Test
        @DisplayName("nombre y email disponibles: devuelve UserInfo")
        void completo() {
            respuestaNombre("Ana");
            respuestaEmail("ana@example.com");

            UserInfo info = service.getUserInfo("u1");

            assertThat(info.getName()).isEqualTo("Ana");
            assertThat(info.getEmail()).isEqualTo("ana@example.com");
        }

        @Test
        @DisplayName("sin nombre: lanza SecurityServiceException y no pide el email")
        void sinNombre() {
            respuestaNombre(null);

            assertThatThrownBy(() -> service.getUserInfo("u1"))
                    .isInstanceOf(SecurityServiceException.class)
                    .hasMessage(MENSAJE);
            verify(restTemplate, never())
                    .exchange(contains("/get-email/"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
        }

        @Test
        @DisplayName("sin email: lanza SecurityServiceException")
        void sinEmail() {
            respuestaNombre("Ana");
            respuestaEmail(null);

            assertThatThrownBy(() -> service.getUserInfo("u1"))
                    .isInstanceOf(SecurityServiceException.class)
                    .hasMessage(MENSAJE);
        }

        @Test
        @DisplayName("ms-security inaccesible: lanza SecurityServiceException conservando la causa")
        void inaccesible() {
            ResourceAccessException causa = new ResourceAccessException("Connection refused");
            when(restTemplate.exchange(contains("/get-name/u1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                    .thenThrow(causa);

            assertThatThrownBy(() -> service.getUserInfo("u1"))
                    .isInstanceOf(SecurityServiceException.class)
                    .hasMessage(MENSAJE)
                    .hasCause(causa);
        }

        @Test
        @DisplayName("ms-security responde 5xx: lanza SecurityServiceException")
        void errorHttp() {
            respuestaNombre("Ana");
            when(restTemplate.exchange(contains("/get-email/u1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                    .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

            assertThatThrownBy(() -> service.getUserInfo("u1"))
                    .isInstanceOf(SecurityServiceException.class)
                    .hasMessage(MENSAJE);
        }
    }
}
