package backend.msclasificator.doctor.application;

import backend.msclasificator.doctor.api.dto.EvaluationAreaCreateDTO;
import backend.msclasificator.doctor.api.dto.EvaluationAreaResponseDTO;
import backend.msclasificator.doctor.api.dto.EvaluationAreaSummaryDTO;
import backend.msclasificator.doctor.api.dto.EvaluationAreaUpdateDTO;
import backend.msclasificator.doctor.api.mapper.EvaluationAreaMapper;
import backend.msclasificator.doctor.application.port.out.EvaluationAreaRepository;
import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.doctor.domain.DoctorArea;
import backend.msclasificator.doctor.domain.EvaluationArea;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageTypeRepository;
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
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationAreaServiceTest {

    private static final PageQuery QUERY = new PageQuery(0, 10, "id", "asc");

    @Mock private EvaluationAreaRepository evaluationAreaRepository;
    @Mock private EvaluationAreaMapper mapper;
    @Mock private MedicalImageTypeRepository medicalImageTypeRepository;

    @InjectMocks private EvaluationAreaService service;

    private EvaluationArea cardio;
    private final EvaluationAreaResponseDTO responseDTO = EvaluationAreaResponseDTO.builder().id(1).build();

    @BeforeEach
    void setUp() {
        cardio = EvaluationArea.builder().id(1).codeArea("CARDIO").name("Cardiología").doctorsCount(3).build();
    }

    private static EvaluationAreaUpdateDTO updateDTO(String code, String name) {
        EvaluationAreaUpdateDTO dto = new EvaluationAreaUpdateDTO();
        dto.setCodeArea(code);
        dto.setName(name);
        return dto;
    }

    @Nested
    @DisplayName("findAll / searchByName / searchByCode / count")
    class Listados {

        @Test
        @DisplayName("findAll: página con el DTO de respuesta")
        void findAll() {
            when(evaluationAreaRepository.findAll(QUERY)).thenReturn(new PageResult<>(List.of(cardio), 0, 10, 1, 1, true));
            when(mapper.toResponseDTO(cardio)).thenReturn(responseDTO);

            ApiResponse<PagedResponse<EvaluationAreaResponseDTO>> r = service.findAll(new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Areas de evaluacion obtenidas exitosamente");
            assertThat(r.getData().getContent()).containsExactly(responseDTO);
        }

        @Test
        @DisplayName("searchByName: página de resúmenes")
        void searchByName() {
            EvaluationAreaSummaryDTO summary = EvaluationAreaSummaryDTO.builder().id(1).build();
            when(evaluationAreaRepository.searchByName("card", QUERY)).thenReturn(new PageResult<>(List.of(cardio), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(cardio)).thenReturn(summary);

            ApiResponse<PagedResponse<EvaluationAreaSummaryDTO>> r = service.searchByName("card", new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Resultados de búsqueda por nombre");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("searchByName con error inesperado: ApiResponse.error con prefijo")
        void searchByNameError() {
            when(evaluationAreaRepository.searchByName("x", QUERY)).thenThrow(new IllegalStateException("BD caída"));

            assertThat(service.searchByName("x", new PageRequestDTO()).getMessage())
                    .isEqualTo("Error al buscar áreas de evaluación por nombre: BD caída");
        }

        @Test
        @DisplayName("searchByCode: página de resúmenes")
        void searchByCode() {
            when(evaluationAreaRepository.searchByCode("CAR", QUERY)).thenReturn(new PageResult<>(List.of(), 0, 10, 0, 0, true));

            ApiResponse<PagedResponse<EvaluationAreaSummaryDTO>> r = service.searchByCode("CAR", new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Resultados de búsqueda por código");
            assertThat(r.getData().getContent()).isEmpty();
        }

        @Test
        @DisplayName("searchByCode con error inesperado: ApiResponse.error con prefijo")
        void searchByCodeError() {
            when(evaluationAreaRepository.searchByCode("x", QUERY)).thenThrow(new IllegalStateException("BD caída"));

            assertThat(service.searchByCode("x", new PageRequestDTO()).getMessage())
                    .isEqualTo("Error al buscar áreas de evaluación por código: BD caída");
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count() {
            when(evaluationAreaRepository.count()).thenReturn(5L);

            assertThat(service.count().getData()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("existente: devuelve el DTO")
        void existe() {
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(mapper.toResponseDTO(cardio)).thenReturn(responseDTO);

            ApiResponse<EvaluationAreaResponseDTO> r = service.findById(1);

            assertThat(r.getData()).isSameAs(responseDTO);
            assertThat(r.getMessage()).isEqualTo("Area de Evaluacion encontrada exitosamente");
        }

        @Test
        @DisplayName("inexistente: error con el ID")
        void noExiste() {
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.findById(1).getMessage()).isEqualTo("Area de Evaluacion no encontrado con ID: 1");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private final EvaluationAreaCreateDTO dto = EvaluationAreaCreateDTO.builder().codeArea("DERM").name("Dermatología").build();

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("nombre duplicado: error sin guardar")
        void nombreDuplicado() {
            when(evaluationAreaRepository.existsByName("Dermatología")).thenReturn(true);

            assertThat(service.create(dto).getMessage()).isEqualTo("Ya existe un área de evaluación con el nombre: Dermatología");
            verify(evaluationAreaRepository, never()).save(any());
        }

        @Test
        @DisplayName("código duplicado: error sin guardar")
        void codigoDuplicado() {
            when(evaluationAreaRepository.existsByName("Dermatología")).thenReturn(false);
            when(evaluationAreaRepository.existsByCodeArea("DERM")).thenReturn(true);

            assertThat(service.create(dto).getMessage()).isEqualTo("Ya existe un área de evaluación con el código: DERM");
            verify(evaluationAreaRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: inicia el contador de doctores en 0 y guarda")
        void valido() {
            EvaluationArea nueva = new EvaluationArea();
            when(evaluationAreaRepository.existsByName("Dermatología")).thenReturn(false);
            when(evaluationAreaRepository.existsByCodeArea("DERM")).thenReturn(false);
            when(mapper.toEntity(dto)).thenReturn(nueva);
            when(evaluationAreaRepository.save(nueva)).thenReturn(nueva);
            when(mapper.toResponseDTO(nueva)).thenReturn(responseDTO);

            ApiResponse<EvaluationAreaResponseDTO> r = service.create(dto);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Área de evaluación creada exitosamente");
            assertThat(nueva.getDoctorsCount()).isZero();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.update(1, null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.update(1, updateDTO("CARDIO", "Cardio")).getMessage()).isEqualTo("Área de evaluación no encontrada con ID: 1");
        }

        @Test
        @DisplayName("nombre de otra área: error sin guardar")
        void nombreDeOtra() {
            EvaluationArea neuro = EvaluationArea.builder().id(3).codeArea("NEURO").name("Neuroimagen").build();
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(evaluationAreaRepository.findByName("Neuroimagen")).thenReturn(Optional.of(neuro));

            assertThat(service.update(1, updateDTO("CARDIO", "Neuroimagen")).getMessage())
                    .isEqualTo("Ya existe un área de evaluación con el nombre: Neuroimagen");
            verify(evaluationAreaRepository, never()).save(any());
        }

        @Test
        @DisplayName("código de otra área: error sin guardar")
        void codigoDeOtra() {
            EvaluationArea neuro = EvaluationArea.builder().id(3).codeArea("NEURO").name("Neuroimagen").build();
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(evaluationAreaRepository.findByName("Cardiología")).thenReturn(Optional.of(cardio));
            when(evaluationAreaRepository.findByCodeArea("NEURO")).thenReturn(Optional.of(neuro));

            assertThat(service.update(1, updateDTO("NEURO", "Cardiología")).getMessage())
                    .isEqualTo("Ya existe un área de evaluación con el código: NEURO");
            verify(evaluationAreaRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: cambia código y nombre, conserva el contador de doctores")
        void valido() {
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(evaluationAreaRepository.findByName("Cardiología adultos")).thenReturn(Optional.empty());
            when(evaluationAreaRepository.findByCodeArea("CARDA")).thenReturn(Optional.empty());
            when(evaluationAreaRepository.save(cardio)).thenReturn(cardio);
            when(mapper.toResponseDTO(cardio)).thenReturn(responseDTO);

            ApiResponse<EvaluationAreaResponseDTO> r = service.update(1, updateDTO("CARDA", "Cardiología adultos"));

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Área de evaluación actualizada exitosamente");
            assertThat(cardio.getCodeArea()).isEqualTo("CARDA");
            assertThat(cardio.getName()).isEqualTo("Cardiología adultos");
            assertThat(cardio.getDoctorsCount()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.delete(1).getMessage()).isEqualTo("Área de evaluación no encontrada con ID: 1");
        }

        @Test
        @DisplayName("con tipos de imagen asociados: error sin eliminar")
        void conTiposDeImagen() {
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(medicalImageTypeRepository.existsByEvaluationAreaId(1)).thenReturn(true);

            ApiResponse<Void> r = service.delete(1);

            assertThat(r.getMessage()).isEqualTo("No se puede eliminar el área de evaluación porque tiene tipos de imágenes médicas asociadas");
            verify(evaluationAreaRepository, never()).delete(any());
        }

        @Test
        @DisplayName("sin doctores asignados: elimina con el mensaje de siempre")
        void sinDoctores() {
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(medicalImageTypeRepository.existsByEvaluationAreaId(1)).thenReturn(false);

            ApiResponse<Void> r = service.delete(1);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Área de evaluación eliminada exitosamente");
            verify(evaluationAreaRepository).delete(cardio);
        }

        @Test
        @DisplayName("con doctores asignados: elimina y avisa cuántas asignaciones se quitaron (bug #21)")
        void conDoctoresAsignados() {
            cardio.setDoctorAreas(List.of(
                    DoctorArea.builder().id(7).doctor(Doctor.builder().id(2).build()).evaluationArea(cardio).build(),
                    DoctorArea.builder().id(8).doctor(Doctor.builder().id(4).build()).evaluationArea(cardio).build()));
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(medicalImageTypeRepository.existsByEvaluationAreaId(1)).thenReturn(false);

            ApiResponse<Void> r = service.delete(1);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Área de evaluación eliminada exitosamente. Se quitaron 2 asignaciones de doctores");
            verify(evaluationAreaRepository).delete(any());
        }

        @Test
        @DisplayName("con un doctor asignado: mensaje en singular")
        void conUnDoctor() {
            cardio.setDoctorAreas(List.of(
                    DoctorArea.builder().id(7).doctor(Doctor.builder().id(2).build()).evaluationArea(cardio).build()));
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(medicalImageTypeRepository.existsByEvaluationAreaId(1)).thenReturn(false);

            assertThat(service.delete(1).getMessage())
                    .isEqualTo("Área de evaluación eliminada exitosamente. Se quitó 1 asignación de doctor");
        }

        @Test
        @DisplayName("violación de integridad: error con prefijo")
        void violacionIntegridad() {
            when(evaluationAreaRepository.findById(1)).thenReturn(Optional.of(cardio));
            when(medicalImageTypeRepository.existsByEvaluationAreaId(1)).thenReturn(false);
            doThrow(new DataIntegrityViolationException("fk")).when(evaluationAreaRepository).delete(cardio);

            assertThat(service.delete(1).getMessage()).isEqualTo("Violacion de integridad de la base de datos: fk");
        }
    }
}
