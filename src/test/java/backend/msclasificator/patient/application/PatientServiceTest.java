package backend.msclasificator.patient.application;

import backend.msclasificator.identity.application.SecurityService;
import backend.msclasificator.identity.application.exception.SecurityServiceException;
import backend.msclasificator.identity.domain.UserInfo;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageRepository;
import backend.msclasificator.patient.api.dto.PatientCreateDTO;
import backend.msclasificator.patient.api.dto.PatientResponseDTO;
import backend.msclasificator.patient.api.dto.PatientSummaryDTO;
import backend.msclasificator.patient.api.dto.PatientUpdateDTO;
import backend.msclasificator.patient.api.mapper.PatientMapper;
import backend.msclasificator.patient.application.port.out.PatientRepository;
import backend.msclasificator.patient.domain.Patient;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
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

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock private PatientRepository patientRepository;
    @Mock private MedicalImageRepository medicalImageRepository;
    @Mock private PatientMapper patientMapper;
    @Mock private SecurityService securityService;

    @InjectMocks private PatientService service;

    private static final String USER_ID = "user-123";

    private Patient patient;
    private PatientResponseDTO responseDTO;
    private PatientSummaryDTO summaryDTO;
    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        patient = Patient.builder().id(1).document("CC12345678").dob(new Date(0)).sex("F").userId(USER_ID).build();
        responseDTO = PatientResponseDTO.builder().id(1).document("CC12345678").userId(USER_ID).build();
        summaryDTO = PatientSummaryDTO.builder().id(1).document("CC12345678").userId(USER_ID).build();
        userInfo = new UserInfo();
        userInfo.setName("Ana");
        userInfo.setEmail("ana@example.com");
    }

    @Nested
    @DisplayName("findAll / searchByDocument / count")
    class Listados {

        @Test
        @DisplayName("findAll: construye el PagedResponse con el resumen")
        void findAll() {
            when(patientRepository.findAll(new PageQuery(0, 10, "id", "asc")))
                    .thenReturn(new PageResult<>(List.of(patient), 0, 10, 1, 1, true));
            when(patientMapper.toSummaryDTO(patient)).thenReturn(summaryDTO);

            ApiResponse<PagedResponse<PatientSummaryDTO>> response = service.findAll(new PageRequestDTO());

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Pacientes encontrados exitosamente");
            assertThat(response.getData().getContent()).containsExactly(summaryDTO);
        }

        @Test
        @DisplayName("searchByDocument: delega en el puerto con el texto y la página")
        void searchByDocument() {
            when(patientRepository.searchByDocument("CC12", new PageQuery(0, 10, "id", "asc")))
                    .thenReturn(new PageResult<>(List.of(patient), 0, 10, 1, 1, true));
            when(patientMapper.toSummaryDTO(patient)).thenReturn(summaryDTO);

            ApiResponse<PagedResponse<PatientSummaryDTO>> response = service.searchByDocument("CC12", new PageRequestDTO());

            assertThat(response.getMessage()).isEqualTo("Resultados de búsqueda por documento");
            assertThat(response.getData().getContent()).containsExactly(summaryDTO);
        }

        @Test
        @DisplayName("count: devuelve el total")
        void count() {
            when(patientRepository.count()).thenReturn(4L);

            assertThat(service.count().getData()).isEqualTo(4L);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("inexistente: error")
        void inexistente() {
            when(patientRepository.findById(9)).thenReturn(Optional.empty());

            assertThat(service.findById(9).getMessage()).isEqualTo("Paciente no encontrado con ID: 9");
        }

        @Test
        @DisplayName("con userInfo de ms-security: éxito con nombre y email")
        void conUserInfo() {
            when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
            when(patientMapper.toResponseDTO(patient)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenReturn(userInfo);

            ApiResponse<PatientResponseDTO> response = service.findById(1);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Paciente encontrado exitosamente");
            assertThat(response.getData().getUserInfo()).isSameAs(userInfo);
        }

        @Test
        @DisplayName("ms-security falla: propaga SecurityServiceException (el handler responde 503)")
        void msSecurityFallaPropaga() {
            when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
            when(patientMapper.toResponseDTO(patient)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenThrow(new SecurityServiceException("sin datos"));

            assertThatThrownBy(() -> service.findById(1))
                    .isInstanceOf(SecurityServiceException.class);
        }
    }

    @Nested
    @DisplayName("findByDocument")
    class FindByDocument {

        @Test
        @DisplayName("inexistente: error con prefijo genérico")
        void inexistente() {
            when(patientRepository.findByDocument("CC1")).thenReturn(Optional.empty());

            assertThat(service.findByDocument("CC1").getMessage())
                    .isEqualTo("Error al buscar pacientes por documento: No se encontro paciente con el documento: CC1");
        }

        @Test
        @DisplayName("con userInfo: éxito (mensaje en plural)")
        void conUserInfo() {
            when(patientRepository.findByDocument("CC12345678")).thenReturn(Optional.of(patient));
            when(patientMapper.toResponseDTO(patient)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenReturn(userInfo);

            ApiResponse<PatientResponseDTO> response = service.findByDocument("CC12345678");

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Pacientes encontrados exitosamente");
            assertThat(response.getData().getUserInfo()).isSameAs(userInfo);
        }

        @Test
        @DisplayName("ms-security falla: propaga SecurityServiceException (no la convierte el catch genérico)")
        void msSecurityFallaPropaga() {
            when(patientRepository.findByDocument("CC12345678")).thenReturn(Optional.of(patient));
            when(patientMapper.toResponseDTO(patient)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenThrow(new SecurityServiceException("sin datos"));

            assertThatThrownBy(() -> service.findByDocument("CC12345678"))
                    .isInstanceOf(SecurityServiceException.class);
        }
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("inexistente: error")
        void inexistente() {
            when(patientRepository.findByUserId("x")).thenReturn(Optional.empty());

            assertThat(service.findByUserId("x").getMessage()).isEqualTo("No se encontro paciente con el userId: x");
        }

        @Test
        @DisplayName("con userInfo: éxito con nombre y email")
        void conUserInfo() {
            when(patientRepository.findByUserId(USER_ID)).thenReturn(Optional.of(patient));
            when(patientMapper.toResponseDTO(patient)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenReturn(userInfo);

            ApiResponse<PatientResponseDTO> response = service.findByUserId(USER_ID);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData().getUserInfo()).isSameAs(userInfo);
        }

        @Test
        @DisplayName("ms-security falla: propaga SecurityServiceException")
        void msSecurityFallaPropaga() {
            when(patientRepository.findByUserId(USER_ID)).thenReturn(Optional.of(patient));
            when(patientMapper.toResponseDTO(patient)).thenReturn(responseDTO);
            when(securityService.getUserInfo(USER_ID)).thenThrow(new SecurityServiceException("sin datos"));

            assertThatThrownBy(() -> service.findByUserId(USER_ID))
                    .isInstanceOf(SecurityServiceException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private final PatientCreateDTO dto = PatientCreateDTO.builder()
                .document("CC12345678").dob(new Date(0)).sex("F").userId(USER_ID).build();

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("documento duplicado: error sin llamar a ms-security")
        void documentoDuplicado() {
            when(patientRepository.findByDocument("CC12345678")).thenReturn(Optional.of(patient));

            assertThat(service.create(dto).getMessage()).isEqualTo("Ya existe un paciente con el documento: CC12345678");
            verify(securityService, never()).existUserById(anyString());
            verify(patientRepository, never()).save(any());
        }

        @Test
        @DisplayName("fecha de nacimiento futura: error")
        void fechaFutura() {
            PatientCreateDTO futuro = PatientCreateDTO.builder()
                    .document("CC12345678").dob(new Date(System.currentTimeMillis() + 86_400_000L)).sex("F").userId(USER_ID).build();
            when(patientRepository.findByDocument("CC12345678")).thenReturn(Optional.empty());

            assertThat(service.create(futuro).getMessage()).isEqualTo("La fecha de nacimiento no puede ser futura");
            verify(patientRepository, never()).save(any());
        }

        @Test
        @DisplayName("userId inexistente en ms-security: error")
        void usuarioInexistente() {
            when(patientRepository.findByDocument("CC12345678")).thenReturn(Optional.empty());
            when(securityService.existUserById(USER_ID)).thenReturn(false);

            assertThat(service.create(dto).getMessage()).isEqualTo("No existe un usuario con el userId: user-123. No se puede crear el paciente sin un usuario asociado.");
            verify(patientRepository, never()).save(any());
        }

        @Test
        @DisplayName("userId ya usado por otro paciente: error sin asignar rol")
        void userIdDuplicado() {
            when(patientRepository.findByDocument("CC12345678")).thenReturn(Optional.empty());
            when(securityService.existUserById(USER_ID)).thenReturn(true);
            when(patientRepository.findByUserId(USER_ID)).thenReturn(Optional.of(patient));

            assertThat(service.create(dto).getMessage())
                    .isEqualTo("Ya existe un paciente con el userId: user-123. El userId debe ser único.");
            verify(securityService, never()).assignDefaultRole(anyString(), anyString());
            verify(patientRepository, never()).save(any());
        }

        @Test
        @DisplayName("ms-security falla al verificar el usuario: propaga SecurityServiceException (503) sin guardar")
        void falloVerificacionUsuario() {
            when(patientRepository.findByDocument("CC12345678")).thenReturn(Optional.empty());
            when(securityService.existUserById(USER_ID)).thenThrow(new SecurityServiceException("caído"));

            assertThatThrownBy(() -> service.create(dto)).isInstanceOf(SecurityServiceException.class);
            verify(patientRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: guarda y, como ÚLTIMO paso, asigna el rol 'patient'")
        void valido() {
            when(patientRepository.findByDocument("CC12345678")).thenReturn(Optional.empty());
            when(securityService.existUserById(USER_ID)).thenReturn(true);
            when(patientRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
            when(patientMapper.toEntity(dto)).thenReturn(patient);
            when(patientRepository.save(patient)).thenReturn(patient);
            when(patientMapper.toSummaryDTO(patient)).thenReturn(summaryDTO);

            ApiResponse<PatientSummaryDTO> response = service.create(dto);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage())
                    .isEqualTo("Paciente creado exitosamente. Rol 'patient' asignado correctamente al usuario.");
            InOrder orden = inOrder(patientRepository, securityService);
            orden.verify(patientRepository).save(patient);
            orden.verify(securityService).assignDefaultRole(USER_ID, "patient");
        }

        @Test
        @DisplayName("si falla la asignación de rol: propaga SecurityServiceException para que @Transactional revierta el guardado")
        void falloRolRevierte() {
            when(patientRepository.findByDocument("CC12345678")).thenReturn(Optional.empty());
            when(securityService.existUserById(USER_ID)).thenReturn(true);
            when(patientRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
            when(patientMapper.toEntity(dto)).thenReturn(patient);
            when(patientRepository.save(patient)).thenReturn(patient);
            doThrow(new SecurityServiceException("rol no asignado")).when(securityService).assignDefaultRole(USER_ID, "patient");

            assertThatThrownBy(() -> service.create(dto)).isInstanceOf(SecurityServiceException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private PatientUpdateDTO dto(String document) {
            PatientUpdateDTO dto = new PatientUpdateDTO();
            dto.setDocument(document);
            dto.setDob(new Date(0));
            dto.setSex("M");
            return dto;
        }

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.update(1, null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("inexistente: error")
        void inexistente() {
            when(patientRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.update(1, dto("CC1")).getMessage()).isEqualTo("Paciente no encontrado con ID: 1");
        }

        @Test
        @DisplayName("documento de otro paciente: error y no guarda")
        void documentoDeOtro() {
            Patient otro = Patient.builder().id(2).document("CC999999").build();
            when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
            when(patientRepository.findByDocument("CC999999")).thenReturn(Optional.of(otro));

            assertThat(service.update(1, dto("CC999999")).getMessage()).isEqualTo("Ya existe un paciente con el documento: CC999999");
            verify(patientRepository, never()).save(any());
        }

        @Test
        @DisplayName("su propio documento: actualiza documento, fecha y sexo")
        void propioDocumento() {
            when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
            when(patientRepository.findByDocument("CC12345678")).thenReturn(Optional.of(patient));
            when(patientRepository.save(patient)).thenReturn(patient);
            when(patientMapper.toSummaryDTO(patient)).thenReturn(summaryDTO);

            ApiResponse<PatientSummaryDTO> response = service.update(1, dto("CC12345678"));

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Paciente actualizado exitosamente");
            assertThat(patient.getSex()).isEqualTo("M");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("inexistente: error y no desvincula ni borra")
        void inexistente() {
            when(patientRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.delete(1).getMessage()).isEqualTo("Paciente no encontrado con ID: 1");
            verify(medicalImageRepository, never()).detachImagesByPatientId(any());
            verify(patientRepository, never()).delete(any());
        }

        @Test
        @DisplayName("existente: desvincula imágenes ANTES de borrar el paciente")
        void desvinculaYBorra() {
            when(patientRepository.findById(1)).thenReturn(Optional.of(patient));

            ApiResponse<Void> response = service.delete(1);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Paciente eliminado exitosamente");
            InOrder orden = inOrder(medicalImageRepository, patientRepository);
            orden.verify(medicalImageRepository).detachImagesByPatientId(1);
            orden.verify(patientRepository).delete(patient);
        }
    }
}
