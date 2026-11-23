package backend.msclasificator.clinicalrecord.application;

import backend.msclasificator.clinicalrecord.api.dto.PrimitiveDatumCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.PrimitiveDatumResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.PrimitiveDatumUpdateDTO;
import backend.msclasificator.clinicalrecord.api.mapper.PrimitiveDatumMapper;
import backend.msclasificator.clinicalrecord.application.port.out.PrimitiveDatumRepository;
import backend.msclasificator.clinicalrecord.domain.PrimitiveDatum;
import backend.msclasificator.clinicalrecord.domain.PrimitiveType;
import backend.msclasificator.clinicalrecord.domain.PrimitiveUnit;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
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
class PrimitiveDatumServiceTest {

    private static final PageQuery QUERY = new PageQuery(0, 10, "id", "asc");

    @Mock private PrimitiveDatumRepository primitiveDatumRepository;
    @Mock private PrimitiveDatumMapper mapper;

    @InjectMocks private PrimitiveDatumService service;

    private final PrimitiveDatum glucemia = PrimitiveDatum.builder()
            .id(5).name("Glucemia").type(PrimitiveType.DOUBLE).unit(PrimitiveUnit.NONE).build();
    private final PrimitiveDatumResponseDTO responseDTO = PrimitiveDatumResponseDTO.builder().id(5).build();

    private static PrimitiveDatumUpdateDTO updateDTO(String name, PrimitiveType type) {
        PrimitiveDatumUpdateDTO dto = new PrimitiveDatumUpdateDTO();
        dto.setName(name);
        dto.setType(type);
        dto.setUnit(PrimitiveUnit.NONE);
        return dto;
    }

    @Nested
    @DisplayName("findAll / count / findById")
    class Lecturas {

        @Test
        @DisplayName("findAll: página con el DTO de respuesta (no el resumen)")
        void findAll() {
            when(primitiveDatumRepository.findAll(QUERY)).thenReturn(new PageResult<>(List.of(glucemia), 0, 10, 1, 1, true));
            when(mapper.toResponseDTO(glucemia)).thenReturn(responseDTO);

            ApiResponse<PagedResponse<PrimitiveDatumResponseDTO>> r = service.findAll(new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Datos primitivos obtenidos exitosamente");
            assertThat(r.getData().getContent()).containsExactly(responseDTO);
        }

        @Test
        @DisplayName("findAll con error inesperado: ApiResponse.error con prefijo")
        void findAllError() {
            when(primitiveDatumRepository.findAll(QUERY)).thenThrow(new IllegalStateException("BD caída"));

            assertThat(service.findAll(new PageRequestDTO()).getMessage()).isEqualTo("Error al listar datos primitivos: BD caída");
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count() {
            when(primitiveDatumRepository.count()).thenReturn(35L);

            assertThat(service.count().getData()).isEqualTo(35L);
        }

        @Test
        @DisplayName("findById existente: devuelve el DTO")
        void findByIdExiste() {
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.of(glucemia));
            when(mapper.toResponseDTO(glucemia)).thenReturn(responseDTO);

            ApiResponse<PrimitiveDatumResponseDTO> r = service.findById(5);

            assertThat(r.getData()).isSameAs(responseDTO);
            assertThat(r.getMessage()).isEqualTo("Dato primitivo encontrado exitosamente");
        }

        @Test
        @DisplayName("findById inexistente: error con el ID")
        void findByIdNoExiste() {
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.empty());

            assertThat(service.findById(5).getMessage()).isEqualTo("Dato primitivo no encontrado con ID: 5");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private final PrimitiveDatumCreateDTO dto = PrimitiveDatumCreateDTO.builder()
                .name("Glucemia").type(PrimitiveType.DOUBLE).unit(PrimitiveUnit.NONE).build();

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("nombre duplicado (sin distinguir mayúsculas): error sin guardar")
        void nombreDuplicado() {
            when(primitiveDatumRepository.findByNameIgnoreCase("Glucemia")).thenReturn(Optional.of(glucemia));

            ApiResponse<PrimitiveDatumResponseDTO> r = service.create(dto);

            assertThat(r.getMessage()).isEqualTo("Ya existe un dato primitivo con el nombre: Glucemia");
            verify(primitiveDatumRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: guarda la entidad del mapper")
        void valido() {
            PrimitiveDatum nuevo = new PrimitiveDatum();
            when(primitiveDatumRepository.findByNameIgnoreCase("Glucemia")).thenReturn(Optional.empty());
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(primitiveDatumRepository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(responseDTO);

            ApiResponse<PrimitiveDatumResponseDTO> r = service.create(dto);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Dato primitivo creado exitosamente");
            assertThat(r.getData()).isSameAs(responseDTO);
        }

        @Test
        @DisplayName("violación de integridad al guardar: error con prefijo")
        void violacionIntegridad() {
            PrimitiveDatum nuevo = new PrimitiveDatum();
            when(primitiveDatumRepository.findByNameIgnoreCase("Glucemia")).thenReturn(Optional.empty());
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(primitiveDatumRepository.save(nuevo)).thenThrow(new DataIntegrityViolationException("dup"));

            assertThat(service.create(dto).getMessage()).isEqualTo("Violacion a integridad de la base de datos: dup");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.update(5, null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.empty());

            assertThat(service.update(5, updateDTO("Glucosa", PrimitiveType.DOUBLE)).getMessage())
                    .isEqualTo("Dato primitivo no encontrado con ID: 5");
        }

        @Test
        @DisplayName("nombre de otro dato primitivo: error sin guardar")
        void nombreDeOtro() {
            PrimitiveDatum otro = PrimitiveDatum.builder().id(6).name("Glucosa").build();
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.of(glucemia));
            when(primitiveDatumRepository.findByNameIgnoreCase("Glucosa")).thenReturn(Optional.of(otro));

            ApiResponse<PrimitiveDatumResponseDTO> r = service.update(5, updateDTO("Glucosa", PrimitiveType.DOUBLE));

            assertThat(r.getMessage()).isEqualTo("Ya existe un dato primitivo con el nombre: Glucosa");
            verify(primitiveDatumRepository, never()).save(any());
        }

        @Test
        @DisplayName("mismo nombre que el propio dato: se permite")
        void mismoNombrePropio() {
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.of(glucemia));
            when(primitiveDatumRepository.findByNameIgnoreCase("Glucemia")).thenReturn(Optional.of(glucemia));
            when(primitiveDatumRepository.save(glucemia)).thenReturn(glucemia);
            when(mapper.toResponseDTO(glucemia)).thenReturn(responseDTO);

            ApiResponse<PrimitiveDatumResponseDTO> r = service.update(5, updateDTO("Glucemia", PrimitiveType.DOUBLE));

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Dato primitivo actualizado exitosamente");
        }

        @Test
        @DisplayName("BUG CONOCIDO #14: cambia el tipo sin revisar los valores ya registrados de pacientes")
        void cambiaTipoSinRevisarValores() {
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.of(glucemia));
            when(primitiveDatumRepository.findByNameIgnoreCase("Glucemia")).thenReturn(Optional.empty());
            when(primitiveDatumRepository.save(glucemia)).thenReturn(glucemia);
            when(mapper.toResponseDTO(glucemia)).thenReturn(responseDTO);

            ApiResponse<PrimitiveDatumResponseDTO> r = service.update(5, updateDTO("Glucemia", PrimitiveType.BOOLEAN));

            assertThat(r.isSuccess()).isTrue();
            assertThat(glucemia.getType()).isEqualTo(PrimitiveType.BOOLEAN);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.empty());

            assertThat(service.delete(5).getMessage()).isEqualTo("Dato primitivo no encontrado con ID: 5");
        }

        @Test
        @DisplayName("válido: elimina (en BD arrastra sus valores de pacientes, ver bug #15)")
        void valido() {
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.of(glucemia));

            ApiResponse<Void> r = service.delete(5);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Dato primitivo eliminado exitosamente");
            verify(primitiveDatumRepository).delete(glucemia);
        }

        @Test
        @DisplayName("violación de integridad: error con prefijo")
        void violacionIntegridad() {
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.of(glucemia));
            doThrow(new DataIntegrityViolationException("fk")).when(primitiveDatumRepository).delete(glucemia);

            assertThat(service.delete(5).getMessage()).isEqualTo("Violacion a integridad de la base de datos: fk");
        }
    }
}
