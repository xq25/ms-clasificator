package backend.msclasificator.doctor.application;

import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.doctor.api.dto.DoctorCreateDTO;
import backend.msclasificator.doctor.api.dto.DoctorResponseDTO;
import backend.msclasificator.doctor.api.dto.DoctorSummaryDTO;
import backend.msclasificator.doctor.api.dto.DoctorUpdateDTO;
import backend.msclasificator.doctor.api.mapper.DoctorMapper;
import backend.msclasificator.doctor.application.port.out.DoctorRepository;
import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.doctor.domain.DoctorArea;
import backend.msclasificator.doctor.domain.EvaluationArea;
import backend.msclasificator.identity.application.port.out.IdentityProvider;
import backend.msclasificator.identity.application.exception.SecurityServiceException;
import backend.msclasificator.identity.domain.UserInfo;
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
import org.springframework.dao.DataIntegrityViolationException;

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
class DoctorServiceTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private DoctorMapper doctorMapper;
    @Mock private ImageClassificationRepository imageClassificationRepository;
    @Mock private IdentityProvider identityProvider;

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
            when(identityProvider.getUserInfo(USER_ID)).thenReturn(userInfo);

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
            when(identityProvider.getUserInfo(USER_ID)).thenThrow(new SecurityServiceException("sin datos"));

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
            when(identityProvider.getUserInfo(USER_ID)).thenThrow(new SecurityServiceException("sin datos"));

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
            when(identityProvider.getUserInfo(USER_ID)).thenReturn(userInfo);

            assertThat(service.findByUserId(USER_ID).getData().getUserInfo()).isSameAs(userInfo);
        }

        @Test
        @DisplayName("ms-security falla: propaga SecurityServiceException")
        void msSecurityFallaPropaga() {
            when(doctorRepository.findByUserId(USER_ID)).thenReturn(Optional.of(doctor));
            when(doctorMapper.toResponseDTO(doctor)).thenReturn(responseDTO);
            when(identityProvider.getUserInfo(USER_ID)).thenThrow(new SecurityServiceException("sin datos"));

            assertThatThrownBy(() -> service.findByUserId(USER_ID)).isInstanceOf(SecurityServiceException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private final DoctorCreateDTO dto = DoctorCreateDTO.builder().code("MED-01").userId(USER_ID).build();

        private void stubValidacionesOk() {
            when(doctorRepository.existsByCode("MED-01")).thenReturn(false);
            when(identityProvider.existsUser(USER_ID)).thenReturn(true);
            when(doctorRepository.existsByUserId(USER_ID)).thenReturn(false);
        }

        @Test
        @DisplayName("usuario inexistente en ms-security: error sin guardar ni asignar rol")
        void usuarioInexistente() {
            when(doctorRepository.existsByCode("MED-01")).thenReturn(false);
            when(identityProvider.existsUser(USER_ID)).thenReturn(false);

            assertThat(service.create(dto).getMessage())
                    .isEqualTo("No existe un usuario con el userId: doc-1. No se puede crear el doctor sin un usuario asociado.");
            verify(doctorRepository, never()).save(any());
            verify(identityProvider, never()).assignDefaultRole(anyString(), anyString());
        }

        @Test
        @DisplayName("ms-security falla al verificar: propaga SecurityServiceException sin guardar")
        void falloVerificacion() {
            when(doctorRepository.existsByCode("MED-01")).thenReturn(false);
            when(identityProvider.existsUser(USER_ID)).thenThrow(new SecurityServiceException("caído"));

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
            InOrder orden = inOrder(doctorRepository, identityProvider);
            orden.verify(doctorRepository).save(doctor);
            orden.verify(identityProvider).assignDefaultRole(USER_ID, "doctor");
        }

        @Test
        @DisplayName("si falla la asignación de rol: propaga SecurityServiceException para que @Transactional revierta el guardado")
        void falloRolRevierte() {
            stubValidacionesOk();
            when(doctorMapper.toEntity(dto)).thenReturn(doctor);
            when(doctorRepository.save(doctor)).thenReturn(doctor);
            doThrow(new SecurityServiceException("rol no asignado")).when(identityProvider).assignDefaultRole(USER_ID, "doctor");

            assertThatThrownBy(() -> service.create(dto)).isInstanceOf(SecurityServiceException.class);
        }
    }

    private static DoctorUpdateDTO updateDTO(String code) {
        DoctorUpdateDTO dto = new DoctorUpdateDTO();
        dto.setCode(code);
        return dto;
    }

    @Nested
    @DisplayName("findAll / searchByCode / count")
    class Listados {

        private final PageQuery query = new PageQuery(0, 10, "id", "asc");

        @Test
        @DisplayName("findAll: página de resúmenes")
        void findAll() {
            DoctorSummaryDTO summary = DoctorSummaryDTO.builder().id(2).build();
            when(doctorRepository.findAll(query)).thenReturn(new PageResult<>(List.of(doctor), 0, 10, 1, 1, true));
            when(doctorMapper.toSummaryDTO(doctor)).thenReturn(summary);

            ApiResponse<PagedResponse<DoctorSummaryDTO>> r = service.findAll(new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Doctores encontrados exitosamente");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("searchByCode: página de resúmenes")
        void searchByCode() {
            when(doctorRepository.searchByCode("MED", query)).thenReturn(new PageResult<>(List.of(), 0, 10, 0, 0, true));

            ApiResponse<PagedResponse<DoctorSummaryDTO>> r = service.searchByCode("MED", new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Resultados de búsqueda por código");
            assertThat(r.getData().getContent()).isEmpty();
        }

        @Test
        @DisplayName("searchByCode con error inesperado: ApiResponse.error con prefijo")
        void searchByCodeError() {
            when(doctorRepository.searchByCode("x", query)).thenThrow(new IllegalStateException("BD caída"));

            assertThat(service.searchByCode("x", new PageRequestDTO()).getMessage())
                    .isEqualTo("Error al buscar doctores por código: BD caída");
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count() {
            when(doctorRepository.count()).thenReturn(6L);

            assertThat(service.count().getData()).isEqualTo(6L);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.update(2, null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(doctorRepository.findById(2)).thenReturn(Optional.empty());

            assertThat(service.update(2, updateDTO("MED-02")).getMessage()).isEqualTo("Doctor no encontrado con ID: 2");
        }

        @Test
        @DisplayName("válido: cambia el código")
        void valido() {
            DoctorSummaryDTO summary = DoctorSummaryDTO.builder().id(2).build();
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(doctorRepository.save(doctor)).thenReturn(doctor);
            when(doctorMapper.toSummaryDTO(doctor)).thenReturn(summary);

            ApiResponse<DoctorSummaryDTO> r = service.update(2, updateDTO("MED-02"));

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Doctor actualizado exitosamente");
            assertThat(doctor.getCode()).isEqualTo("MED-02");
        }

        @Test
        @DisplayName("código nulo: conserva el código actual")
        void codigoNulo() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(doctorRepository.save(doctor)).thenReturn(doctor);

            assertThat(service.update(2, updateDTO(null)).isSuccess()).isTrue();
            assertThat(doctor.getCode()).isEqualTo("MED-01");
        }

        @Test
        @DisplayName("BUG CONOCIDO #19: acepta el código de otro doctor sin validarlo (create sí lo valida)")
        void codigoDeOtroDoctor() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(doctorRepository.save(doctor)).thenReturn(doctor);

            ApiResponse<DoctorSummaryDTO> r = service.update(2, updateDTO("MED-99"));

            assertThat(r.isSuccess()).isTrue();
            assertThat(doctor.getCode()).isEqualTo("MED-99");
            verify(doctorRepository, never()).existsByCode(anyString());
            verify(doctorRepository, never()).findByCode(anyString());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(doctorRepository.findById(2)).thenReturn(Optional.empty());

            assertThat(service.delete(2).getMessage()).isEqualTo("Doctor no encontrado con ID: 2");
        }

        @Test
        @DisplayName("con clasificaciones de imágenes: error sin eliminar")
        void conClasificaciones() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(imageClassificationRepository.existsByDoctorId(2)).thenReturn(true);

            ApiResponse<Void> r = service.delete(2);

            assertThat(r.getMessage()).isEqualTo("No se puede eliminar el doctor porque tiene diagnosticos asociados");
            verify(doctorRepository, never()).delete(any());
        }

        @Test
        @DisplayName("sin clasificaciones: elimina")
        void valido() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(imageClassificationRepository.existsByDoctorId(2)).thenReturn(false);

            ApiResponse<Void> r = service.delete(2);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Doctor eliminado exitosamente");
            verify(doctorRepository).delete(doctor);
        }

        @Test
        @DisplayName("BUG CONOCIDO #20: con áreas asignadas, la cascada borra las asignaciones pero no descuenta doctorsCount")
        void noDescuentaContador() {
            EvaluationArea cardio = EvaluationArea.builder().id(1).codeArea("CARDIO").doctorsCount(3).build();
            DoctorArea asignacion = DoctorArea.builder().id(7).doctor(doctor).evaluationArea(cardio).build();
            doctor.setDoctorAreas(List.of(asignacion));
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(imageClassificationRepository.existsByDoctorId(2)).thenReturn(false);

            assertThat(service.delete(2).isSuccess()).isTrue();
            assertThat(cardio.getDoctorsCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("violación de integridad: error con prefijo")
        void violacionIntegridad() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(imageClassificationRepository.existsByDoctorId(2)).thenReturn(false);
            doThrow(new DataIntegrityViolationException("fk")).when(doctorRepository).delete(doctor);

            assertThat(service.delete(2).getMessage()).isEqualTo("Violacion de integridad en la base de datos: fk");
        }
    }
}
