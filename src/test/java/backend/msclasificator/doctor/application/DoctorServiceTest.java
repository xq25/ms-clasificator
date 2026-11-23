package backend.msclasificator.doctor.application;

import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.doctor.api.dto.DoctorCreateDTO;
import backend.msclasificator.doctor.api.dto.DoctorResponseDTO;
import backend.msclasificator.doctor.api.dto.DoctorSummaryDTO;
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
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    @Nested
    @DisplayName("create")
    class Create {

        private final DoctorCreateDTO dto = DoctorCreateDTO.builder().code("MED-01").userId(USER_ID).build();

        private void stubValidacionesOk() {
            when(doctorRepository.existsByCode("MED-01")).thenReturn(false);
            when(securityService.existUserById(USER_ID)).thenReturn(true);
            when(doctorRepository.existsByUserId(USER_ID)).thenReturn(false);
        }

        @Test
        @DisplayName("usuario inexistente en ms-security: error sin guardar ni asignar rol")
        void usuarioInexistente() {
            when(doctorRepository.existsByCode("MED-01")).thenReturn(false);
            when(securityService.existUserById(USER_ID)).thenReturn(false);

            assertThat(service.create(dto).getMessage())
                    .isEqualTo("No existe un usuario con el userId: doc-1. No se puede crear el doctor sin un usuario asociado.");
            verify(doctorRepository, never()).save(any());
            verify(securityService, never()).assignDefaultRole(anyString(), anyString());
        }

        @Test
        @DisplayName("ms-security falla al verificar: propaga SecurityServiceException sin guardar")
        void falloVerificacion() {
            when(doctorRepository.existsByCode("MED-01")).thenReturn(false);
            when(securityService.existUserById(USER_ID)).thenThrow(new SecurityServiceException("caído"));

            assertThatThrownBy(() -> service.create(dto)).isInstanceOf(SecurityServiceException.class);
            verify(doctorRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: guarda y, como ÚLTIMO paso, asigna el rol 'doctor'")
        void valido() {
            stubValidacionesOk();
            when(doctorMapper.toEntity(dto)).thenReturn(doctor);
            when(doctorRepository.save(doctor)).thenReturn(doctor);
            when(doctorMapper.toSummaryDTO(doctor)).thenReturn(new DoctorSummaryDTO());

            ApiResponse<DoctorSummaryDTO> response = service.create(dto);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Doctor creado exitosamente. Rol 'doctor' asignado correctamente al usuario.");
            InOrder orden = inOrder(doctorRepository, securityService);
            orden.verify(doctorRepository).save(doctor);
            orden.verify(securityService).assignDefaultRole(USER_ID, "doctor");
        }

        @Test
        @DisplayName("si falla la asignación de rol: propaga SecurityServiceException para que @Transactional revierta el guardado")
        void falloRolRevierte() {
            stubValidacionesOk();
            when(doctorMapper.toEntity(dto)).thenReturn(doctor);
            when(doctorRepository.save(doctor)).thenReturn(doctor);
            doThrow(new SecurityServiceException("rol no asignado")).when(securityService).assignDefaultRole(USER_ID, "doctor");

            assertThatThrownBy(() -> service.create(dto)).isInstanceOf(SecurityServiceException.class);
        }
    }
}
