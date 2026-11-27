package backend.msclasificator.doctor.application;

import backend.msclasificator.doctor.api.dto.DoctorAreaCreateDTO;
import backend.msclasificator.doctor.api.dto.DoctorAreaResponseDTO;
import backend.msclasificator.doctor.api.dto.DoctorAreaSummaryDTO;
import backend.msclasificator.doctor.api.mapper.DoctorAreaMapper;
import backend.msclasificator.doctor.application.port.out.DoctorAreaRepository;
import backend.msclasificator.doctor.application.port.out.DoctorRepository;
import backend.msclasificator.doctor.application.port.out.EvaluationAreaRepository;
import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.doctor.domain.DoctorArea;
import backend.msclasificator.doctor.domain.EvaluationArea;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorAreaServiceTest {

    private static final PageQuery QUERY = new PageQuery(0, 10, "id", "asc");

    @Mock private DoctorAreaRepository doctorAreaRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private EvaluationAreaRepository evaluationAreaRepository;
    @Mock private DoctorAreaMapper mapper;

    @InjectMocks private DoctorAreaService service;

    private Doctor doctor;
    private EvaluationArea cardio;
    private DoctorArea relacion;
    private final DoctorAreaSummaryDTO summary = DoctorAreaSummaryDTO.builder().id(7).build();
    private final DoctorAreaResponseDTO responseDTO = DoctorAreaResponseDTO.builder().id(7).build();

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder().id(2).code("MED-01").userId("doc-1").build();
        cardio = EvaluationArea.builder().id(1).codeArea("CARDIO").name("Cardiología").doctorsCount(3).build();
        relacion = DoctorArea.builder().id(7).doctor(doctor).evaluationArea(cardio).build();
    }

    @Nested
    @DisplayName("findAll / count / findById")
    class Lecturas {

        @Test
        @DisplayName("findAll: página de resúmenes")
        void findAll() {
            when(doctorAreaRepository.findAll(QUERY)).thenReturn(new PageResult<>(List.of(relacion), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(relacion)).thenReturn(summary);

            ApiResponse<PagedResponse<DoctorAreaSummaryDTO>> r = service.findAll(new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Relaciones DoctorArea obtenidas exitosamente");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count() {
            when(doctorAreaRepository.count()).thenReturn(4L);

            assertThat(service.count().getData()).isEqualTo(4L);
        }

        @Test
        @DisplayName("findById existente: devuelve el DTO")
        void findByIdExiste() {
            when(doctorAreaRepository.findById(7)).thenReturn(Optional.of(relacion));
            when(mapper.toResponseDTO(relacion)).thenReturn(responseDTO);

            ApiResponse<DoctorAreaResponseDTO> r = service.findById(7);

            assertThat(r.getData()).isSameAs(responseDTO);
            assertThat(r.getMessage()).isEqualTo("Relacion encontrada exitosamente");
        }

        @Test
        @DisplayName("findById inexistente: error con el ID")
        void findByIdNoExiste() {
            when(doctorAreaRepository.findById(7)).thenReturn(Optional.empty());

            assertThat(service.findById(7).getMessage()).isEqualTo("Relación DoctorArea no encontrada con ID: 7");
        }
    }

    @Nested
    @DisplayName("findByDoctorId / findByEvaluationAreaId")
    class PorDoctorYArea {

        @Test
        @DisplayName("doctor inexistente: error")
        void doctorInexistente() {
            when(doctorRepository.existsById(2)).thenReturn(false);

            ApiResponse<List<DoctorAreaSummaryDTO>> r = service.findByDoctorId(2);

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("No se encontro un doctor con id : 2");
            verify(doctorAreaRepository, never()).findByDoctorId(any());
        }

        @Test
        @DisplayName("doctor con áreas: lista de resúmenes")
        void doctorConAreas() {
            when(doctorRepository.existsById(2)).thenReturn(true);
            when(doctorAreaRepository.findByDoctorId(2)).thenReturn(List.of(relacion));
            when(mapper.toSummaryDTO(relacion)).thenReturn(summary);

            ApiResponse<List<DoctorAreaSummaryDTO>> r = service.findByDoctorId(2);

            assertThat(r.getMessage()).isEqualTo("Relaciones encontradas exitosamente");
            assertThat(r.getData()).containsExactly(summary);
        }

        @Test
        @DisplayName("doctor sin áreas: éxito con lista vacía y mensaje de vacío")
        void doctorSinAreas() {
            when(doctorRepository.existsById(2)).thenReturn(true);
            when(doctorAreaRepository.findByDoctorId(2)).thenReturn(List.of());

            ApiResponse<List<DoctorAreaSummaryDTO>> r = service.findByDoctorId(2);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("No se encontraron áreas de evaluación para este doctor");
        }

        @Test
        @DisplayName("área inexistente: error")
        void areaInexistente() {
            when(evaluationAreaRepository.existsById(1)).thenReturn(false);

            ApiResponse<List<DoctorAreaSummaryDTO>> r = service.findByEvaluationAreaId(1);

            assertThat(r.getMessage()).isEqualTo("No se encontro un area de evaluacion con id : 1");
        }

        @Test
        @DisplayName("área con doctores: lista de resúmenes")
        void areaConDoctores() {
            when(evaluationAreaRepository.existsById(1)).thenReturn(true);
            when(doctorAreaRepository.findByEvaluationAreaId(1)).thenReturn(List.of(relacion));
            when(mapper.toSummaryDTO(relacion)).thenReturn(summary);

            ApiResponse<List<DoctorAreaSummaryDTO>> r = service.findByEvaluationAreaId(1);

            assertThat(r.getMessage()).isEqualTo("Relaciones encontradas exitosamente");
            assertThat(r.getData()).containsExactly(summary);
        }

        @Test
        @DisplayName("área sin doctores: éxito con mensaje de vacío")
        void areaSinDoctores() {
            when(evaluationAreaRepository.existsById(1)).thenReturn(true);
            when(doctorAreaRepository.findByEvaluationAreaId(1)).thenReturn(List.of());

            assertThat(service.findByEvaluationAreaId(1).getMessage()).isEqualTo("No se encontraron doctores para esta área de evaluación");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private final DoctorAreaCreateDTO dto = DoctorAreaCreateDTO.builder().doctorId(2).evaluationAreaId(1).build();

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("doctor inexistente: error sin guardar")
        void doctorInexistente() {
            when(doctorRepository.findById(2)).thenReturn(Optional.empty());

            assertThat(service.create(dto).getMessage()).isEqualTo("Doctor no encontrado con ID: 2");
            verify(doctorAreaRepository, never()).save(any());
        }

        @Test
        @DisplayName("área inexistente: error sin guardar")
        void areaInexistente() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.create(dto).getMessage()).isEqualTo("Área de evaluación no encontrada con ID: 1");
            verify(doctorAreaRepository, never()).save(any());
        }

        @Test
        @DisplayName("relación duplicada: error sin guardar ni tocar el contador")
        void duplicada() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(true);

            ApiResponse<DoctorAreaResponseDTO> r = service.create(dto);

            assertThat(r.getMessage()).isEqualTo("Este doctor ya está asociado a esta área de evaluación");
            assertThat(cardio.getDoctorsCount()).isEqualTo(3);
            verify(doctorAreaRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: asigna doctor y área, incrementa el contador y guarda")
        void valido() {
            DoctorArea nueva = new DoctorArea();
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(false);
            when(mapper.toEntity(dto)).thenReturn(nueva);
            when(doctorAreaRepository.save(nueva)).thenReturn(nueva);
            when(mapper.toResponseDTO(nueva)).thenReturn(responseDTO);

            ApiResponse<DoctorAreaResponseDTO> r = service.create(dto);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Doctor asociado al área de evaluación exitosamente");
            assertThat(nueva.getDoctor()).isSameAs(doctor);
            assertThat(nueva.getEvaluationArea()).isSameAs(cardio);
            assertThat(cardio.getDoctorsCount()).isEqualTo(4);
        }
    }

    @Nested
    @DisplayName("delete / deleteByDoctorAndArea")
    class Eliminar {

        @Test
        @DisplayName("delete inexistente: error")
        void deleteNoExiste() {
            when(doctorAreaRepository.findById(7)).thenReturn(Optional.empty());

            assertThat(service.delete(7).getMessage()).isEqualTo("Relación DoctorArea no encontrada con ID: 7");
        }

        @Test
        @DisplayName("delete válido: decrementa el contador y elimina")
        void deleteValido() {
            when(doctorAreaRepository.findById(7)).thenReturn(Optional.of(relacion));

            ApiResponse<Void> r = service.delete(7);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Relación DoctorArea eliminada exitosamente");
            assertThat(cardio.getDoctorsCount()).isEqualTo(2);
            verify(doctorAreaRepository).delete(relacion);
        }

        @Test
        @DisplayName("deleteByDoctorAndArea sin relación: error")
        void porDoctorYAreaSinRelacion() {
            when(doctorAreaRepository.findByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(Optional.empty());

            assertThat(service.deleteByDoctorAndArea(2, 1).getMessage())
                    .isEqualTo("No existe relación entre este doctor y esta área de evaluación");
            verify(doctorAreaRepository, never()).delete(any());
        }

        @Test
        @DisplayName("deleteByDoctorAndArea válido: decrementa el contador y elimina")
        void porDoctorYAreaValido() {
            when(doctorAreaRepository.findByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(Optional.of(relacion));

            ApiResponse<Void> r = service.deleteByDoctorAndArea(2, 1);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Doctor disociado del área de evaluación exitosamente");
            assertThat(cardio.getDoctorsCount()).isEqualTo(2);
            verify(doctorAreaRepository).delete(relacion);
        }

        @Test
        @DisplayName("deleteByDoctorAndArea con error inesperado: ApiResponse.error con prefijo")
        void porDoctorYAreaError() {
            when(doctorAreaRepository.findByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(Optional.of(relacion));
            doThrow(new IllegalStateException("BD caída")).when(doctorAreaRepository).delete(relacion);

            assertThat(service.deleteByDoctorAndArea(2, 1).getMessage())
                    .isEqualTo("Error al disociar doctor del área de evaluación: BD caída");
        }
    }
}
