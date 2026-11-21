package backend.msclasificator.doctor.application;

import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.doctor.api.dto.DoctorResponseDTO;
import backend.msclasificator.doctor.api.mapper.DoctorMapper;
import backend.msclasificator.doctor.application.port.out.DoctorRepository;
import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.identity.application.SecurityService;
import backend.msclasificator.identity.application.exception.SecurityServiceException;
import backend.msclasificator.identity.domain.UserInfo;
import backend.msclasificator.shared.api.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

// Cubre solo las búsquedas que enriquecen con datos de ms-security; el resto de DoctorService queda pendiente (ADR-008)
@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private DoctorMapper doctorMapper;
    @Mock private ImageClassificationRepository imageClassificationRepository;
    @Mock private SecurityService securityService;

    @InjectMocks private DoctorService service;

    private static final String USER_ID = "doc-1";

    private Doctor doctor;
    private DoctorResponseDTO responseDTO;
    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder().id(2).code("MED-01").userId(USER_ID).build();
        responseDTO = DoctorResponseDTO.builder().id(2).code("MED-01").userId(USER_ID).build();
        userInfo = new UserInfo();
        userInfo.setName("Luis");
        userInfo.setEmail("luis@example.com");
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("inexistente: error")
        void inexistente() {
            when(doctorRepository.findById(9)).thenReturn(Optional.empty());

            assertThat(service.findById(9).getMessage()).isEqualTo("Doctor no encontrado con ID: 9");
        }

        @Test
        @DisplayName("con userInfo: éxito con nombre y email")
        void conUserInfo() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(doctorMapper.toResponseDTO(doctor)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenReturn(userInfo);

            ApiResponse<DoctorResponseDTO> response = service.findById(2);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Doctor encontrado exitosamente");
            assertThat(response.getData().getUserInfo()).isSameAs(userInfo);
        }

        @Test
        @DisplayName("ms-security falla: propaga SecurityServiceException (el handler responde 503)")
        void msSecurityFallaPropaga() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(doctorMapper.toResponseDTO(doctor)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenThrow(new SecurityServiceException("sin datos"));

            assertThatThrownBy(() -> service.findById(2)).isInstanceOf(SecurityServiceException.class);
        }
    }

    @Nested
    @DisplayName("findByCode")
    class FindByCode {

        @Test
        @DisplayName("inexistente: error")
        void inexistente() {
            when(doctorRepository.findByCode("X")).thenReturn(Optional.empty());

            assertThat(service.findByCode("X").getMessage()).isEqualTo("Doctor no encontrado con código: X");
        }

        @Test
        @DisplayName("ms-security falla: propaga SecurityServiceException")
        void msSecurityFallaPropaga() {
            when(doctorRepository.findByCode("MED-01")).thenReturn(Optional.of(doctor));
            when(doctorMapper.toResponseDTO(doctor)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenThrow(new SecurityServiceException("sin datos"));

            assertThatThrownBy(() -> service.findByCode("MED-01")).isInstanceOf(SecurityServiceException.class);
        }
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("con userInfo: éxito con nombre y email")
        void conUserInfo() {
            when(doctorRepository.findByUserId(USER_ID)).thenReturn(Optional.of(doctor));
            when(doctorMapper.toResponseDTO(doctor)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenReturn(userInfo);

            assertThat(service.findByUserId(USER_ID).getData().getUserInfo()).isSameAs(userInfo);
        }

        @Test
        @DisplayName("ms-security falla: propaga SecurityServiceException")
        void msSecurityFallaPropaga() {
            when(doctorRepository.findByUserId(USER_ID)).thenReturn(Optional.of(doctor));
            when(doctorMapper.toResponseDTO(doctor)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenThrow(new SecurityServiceException("sin datos"));

            assertThatThrownBy(() -> service.findByUserId(USER_ID)).isInstanceOf(SecurityServiceException.class);
        }
    }
}
