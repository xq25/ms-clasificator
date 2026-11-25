package backend.msclasificator.diagnosiscatalog.application;

import backend.msclasificator.classification.application.port.out.ClassificationDiagnosisRepository;
import backend.msclasificator.dataset.application.port.out.CategoryDiagnosticRepository;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticCreateDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticResponseDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticUpdateDTO;
import backend.msclasificator.diagnosiscatalog.api.mapper.MedicalDiagnosticMapper;
import backend.msclasificator.diagnosiscatalog.application.port.out.MedicalDiagnosticRepository;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicalDiagnosticServiceTest {

    private static final PageQuery QUERY = new PageQuery(0, 10, "id", "asc");

    @Mock private MedicalDiagnosticRepository medicalDiagnosticRepository;
    @Mock private ClassificationDiagnosisRepository classificationDiagnosisRepository;
    @Mock private MedicalDiagnosticMapper mapper;
    @Mock private CategoryDiagnosticRepository categoryDiagnosticRepository;

    @InjectMocks private MedicalDiagnosticService service;

    // Jerarquía CIE-10 de ejemplo: I20-I25 (padre) → I21 (hijo)
    private MedicalDiagnostic cardiopatiaIsquemica;
    private MedicalDiagnostic infarto;
    private final MedicalDiagnosticResponseDTO responseDTO = new MedicalDiagnosticResponseDTO();

    @BeforeEach
    void setUp() {
        cardiopatiaIsquemica = MedicalDiagnostic.builder().id(1).diagnosticCode("I20").diagnosticName("Cardiopatía isquémica").build();
        infarto = MedicalDiagnostic.builder().id(2).diagnosticCode("I21").diagnosticName("Infarto agudo de miocardio")
                .parentDiagnostic(cardiopatiaIsquemica).build();
    }

    private static MedicalDiagnosticUpdateDTO updateDTO(String code, String name) {
        MedicalDiagnosticUpdateDTO dto = new MedicalDiagnosticUpdateDTO();
        dto.setDiagnosticCode(code);
        dto.setDiagnosticName(name);
        return dto;
    }

    @Nested
    @DisplayName("findAll / searchByName / searchByCode / count")
    class Listados {

        @Test
        @DisplayName("findAll: página con el DTO de respuesta")
        void findAll() {
            when(medicalDiagnosticRepository.findAll(QUERY)).thenReturn(new PageResult<>(List.of(infarto), 0, 10, 1, 1, true));
            when(mapper.toResponseDTO(infarto)).thenReturn(responseDTO);

            ApiResponse<PagedResponse<MedicalDiagnosticResponseDTO>> r = service.findAll(new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Diagnósticos médicos encontrados exitosamente");
            assertThat(r.getData().getContent()).containsExactly(responseDTO);
        }

        @Test
        @DisplayName("searchByName: página de resúmenes")
        void searchByName() {
            MedicalDiagnosticSummaryDTO summary = new MedicalDiagnosticSummaryDTO();
            when(medicalDiagnosticRepository.searchByName("infarto", QUERY)).thenReturn(new PageResult<>(List.of(infarto), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(infarto)).thenReturn(summary);

            ApiResponse<PagedResponse<MedicalDiagnosticSummaryDTO>> r = service.searchByName("infarto", new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Resultados de búsqueda por nombre");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("searchByName con error inesperado: ApiResponse.error con prefijo")
        void searchByNameError() {
            when(medicalDiagnosticRepository.searchByName("x", QUERY)).thenThrow(new IllegalStateException("BD caída"));

            assertThat(service.searchByName("x", new PageRequestDTO()).getMessage())
                    .isEqualTo("Error al buscar diagnósticos por nombre: BD caída");
        }

        @Test
        @DisplayName("searchByCode: página de resúmenes")
        void searchByCode() {
            when(medicalDiagnosticRepository.searchByCode("I2", QUERY)).thenReturn(new PageResult<>(List.of(), 0, 10, 0, 0, true));

            ApiResponse<PagedResponse<MedicalDiagnosticSummaryDTO>> r = service.searchByCode("I2", new PageRequestDTO());

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Resultados de búsqueda por código");
            assertThat(r.getData().getContent()).isEmpty();
        }

        @Test
        @DisplayName("searchByCode con error inesperado: ApiResponse.error con prefijo")
        void searchByCodeError() {
            when(medicalDiagnosticRepository.searchByCode("x", QUERY)).thenThrow(new IllegalStateException("BD caída"));

            assertThat(service.searchByCode("x", new PageRequestDTO()).getMessage())
                    .isEqualTo("Error al buscar diagnósticos por código: BD caída");
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count() {
            when(medicalDiagnosticRepository.count()).thenReturn(292L);

            assertThat(service.count().getData()).isEqualTo(292L);
        }
    }

    @Nested
    @DisplayName("findById / findByParentDiagnosticId")
    class Busquedas {

        @Test
        @DisplayName("findById existente: devuelve el DTO")
        void findByIdExiste() {
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(mapper.toResponseDTO(infarto)).thenReturn(responseDTO);

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.findById(2);

            assertThat(r.getData()).isSameAs(responseDTO);
            assertThat(r.getMessage()).isEqualTo("Diagnóstico médico encontrado");
        }

        @Test
        @DisplayName("findById inexistente: error con el ID")
        void findByIdNoExiste() {
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.empty());

            assertThat(service.findById(2).getMessage()).isEqualTo("Diagnóstico médico no encontrado con ID: 2");
        }

        @Test
        @DisplayName("findByParentDiagnosticId con padre inexistente: error")
        void hijosPadreInexistente() {
            when(medicalDiagnosticRepository.existsById(1)).thenReturn(false);

            ApiResponse<List<MedicalDiagnosticSummaryDTO>> r = service.findByParentDiagnosticId(1);

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("No se encontró un diagnóstico padre con ID: 1");
        }

        @Test
        @DisplayName("findByParentDiagnosticId: lista de hijos en resumen")
        void hijos() {
            MedicalDiagnosticSummaryDTO summary = new MedicalDiagnosticSummaryDTO();
            when(medicalDiagnosticRepository.existsById(1)).thenReturn(true);
            when(medicalDiagnosticRepository.findByParentDiagnosticId(1)).thenReturn(List.of(infarto));
            when(mapper.toSummaryDTO(infarto)).thenReturn(summary);

            ApiResponse<List<MedicalDiagnosticSummaryDTO>> r = service.findByParentDiagnosticId(1);

            assertThat(r.getMessage()).isEqualTo("Diagnósticos médicos encontrados para el diagnóstico padre ID: 1");
            assertThat(r.getData()).containsExactly(summary);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("código duplicado: error sin guardar")
        void codigoDuplicado() {
            MedicalDiagnosticCreateDTO dto = MedicalDiagnosticCreateDTO.builder().diagnosticCode("I21").diagnosticName("x").build();
            when(medicalDiagnosticRepository.findByDiagnosticCode("I21")).thenReturn(Optional.of(infarto));

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.create(dto);

            assertThat(r.getMessage()).isEqualTo("Ya existe un diagnóstico con el código: I21");
            verify(medicalDiagnosticRepository, never()).save(any());
        }

        @Test
        @DisplayName("sin padre: guarda como diagnóstico raíz")
        void sinPadre() {
            MedicalDiagnosticCreateDTO dto = MedicalDiagnosticCreateDTO.builder().diagnosticCode("I50").diagnosticName("Insuficiencia cardíaca").build();
            MedicalDiagnostic nuevo = new MedicalDiagnostic();
            when(medicalDiagnosticRepository.findByDiagnosticCode("I50")).thenReturn(Optional.empty());
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(medicalDiagnosticRepository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(responseDTO);

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.create(dto);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Diagnóstico médico creado exitosamente");
            assertThat(nuevo.getParentDiagnostic()).isNull();
        }

        @Test
        @DisplayName("con padre existente: lo asigna y guarda")
        void conPadre() {
            MedicalDiagnosticCreateDTO dto = MedicalDiagnosticCreateDTO.builder().diagnosticCode("I22").diagnosticName("Infarto subsecuente").parentDiagnosticId(1).build();
            MedicalDiagnostic nuevo = new MedicalDiagnostic();
            when(medicalDiagnosticRepository.findByDiagnosticCode("I22")).thenReturn(Optional.empty());
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(medicalDiagnosticRepository.findById(1)).thenReturn(Optional.of(cardiopatiaIsquemica));
            when(medicalDiagnosticRepository.save(nuevo)).thenReturn(nuevo);

            assertThat(service.create(dto).isSuccess()).isTrue();
            assertThat(nuevo.getParentDiagnostic()).isSameAs(cardiopatiaIsquemica);
        }

        @Test
        @DisplayName("con padre inexistente: error sin guardar")
        void padreInexistente() {
            MedicalDiagnosticCreateDTO dto = MedicalDiagnosticCreateDTO.builder().diagnosticCode("I22").diagnosticName("x").parentDiagnosticId(99).build();
            when(medicalDiagnosticRepository.findByDiagnosticCode("I22")).thenReturn(Optional.empty());
            when(mapper.toEntity(dto)).thenReturn(new MedicalDiagnostic());
            when(medicalDiagnosticRepository.findById(99)).thenReturn(Optional.empty());

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.create(dto);

            assertThat(r.getMessage()).isEqualTo("Diagnóstico padre no encontrado con ID: 99");
            verify(medicalDiagnosticRepository, never()).save(any());
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
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.empty());

            assertThat(service.update(2, updateDTO("I21", "x")).getMessage()).isEqualTo("Diagnóstico médico no encontrado con ID: 2");
        }

        @Test
        @DisplayName("código de otro diagnóstico: error sin guardar")
        void codigoDeOtro() {
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(medicalDiagnosticRepository.findByDiagnosticCode("I20")).thenReturn(Optional.of(cardiopatiaIsquemica));

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.update(2, updateDTO("I20", "x"));

            assertThat(r.getMessage()).isEqualTo("Ya existe un diagnóstico con el código: I20");
            verify(medicalDiagnosticRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: cambia código y nombre, conserva el padre")
        void valido() {
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(medicalDiagnosticRepository.findByDiagnosticCode("I21")).thenReturn(Optional.of(infarto));
            when(medicalDiagnosticRepository.save(infarto)).thenReturn(infarto);
            when(mapper.toResponseDTO(infarto)).thenReturn(responseDTO);

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.update(2, updateDTO("I21", "IAM"));

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Diagnóstico médico actualizado exitosamente");
            assertThat(infarto.getDiagnosticName()).isEqualTo("IAM");
            assertThat(infarto.getParentDiagnostic()).isSameAs(cardiopatiaIsquemica);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(medicalDiagnosticRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.delete(1).getMessage()).isEqualTo("Diagnóstico médico no encontrado con ID: 1");
        }

        @Test
        @DisplayName("con sub-diagnósticos: error sin eliminar")
        void conHijos() {
            when(medicalDiagnosticRepository.findById(1)).thenReturn(Optional.of(cardiopatiaIsquemica));
            when(medicalDiagnosticRepository.existsByParentDiagnosticId(1)).thenReturn(true);

            ApiResponse<Void> r = service.delete(1);

            assertThat(r.getMessage()).isEqualTo("El diagnóstico médico no se puede eliminar porque tiene sub-diagnósticos asociados.");
            verify(medicalDiagnosticRepository, never()).delete(any());
        }

        @Test
        @DisplayName("usado en clasificaciones de imágenes: error sin eliminar")
        void usadoEnClasificaciones() {
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(medicalDiagnosticRepository.existsByParentDiagnosticId(2)).thenReturn(false);
            when(classificationDiagnosisRepository.existsByMedicalDiagnosticId(2)).thenReturn(true);

            ApiResponse<Void> r = service.delete(2);

            assertThat(r.getMessage()).isEqualTo("No se puede eliminar el diagnóstico ya que está asociado a clasificaciones de imágenes.");
            verify(medicalDiagnosticRepository, never()).delete(any());
        }

        @Test
        @DisplayName("sin hijos ni clasificaciones: elimina")
        void valido() {
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(medicalDiagnosticRepository.existsByParentDiagnosticId(2)).thenReturn(false);
            when(classificationDiagnosisRepository.existsByMedicalDiagnosticId(2)).thenReturn(false);

            ApiResponse<Void> r = service.delete(2);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Diagnóstico médico eliminado exitosamente");
            verify(medicalDiagnosticRepository).delete(infarto);
        }

        @Test
        @DisplayName("BUG CONOCIDO #17: no revisa si el diagnóstico definió una categoría de dataset antes de eliminar")
        void noRevisaCategorias() {
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(medicalDiagnosticRepository.existsByParentDiagnosticId(2)).thenReturn(false);
            when(classificationDiagnosisRepository.existsByMedicalDiagnosticId(2)).thenReturn(false);

            service.delete(2);

            verifyNoInteractions(categoryDiagnosticRepository);
            verify(medicalDiagnosticRepository).delete(infarto);
        }

        @Test
        @DisplayName("violación de integridad lanzada en el delete: error con prefijo")
        void violacionIntegridad() {
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(medicalDiagnosticRepository.existsByParentDiagnosticId(2)).thenReturn(false);
            when(classificationDiagnosisRepository.existsByMedicalDiagnosticId(2)).thenReturn(false);
            doThrow(new DataIntegrityViolationException("fk")).when(medicalDiagnosticRepository).delete(infarto);

            assertThat(service.delete(2).getMessage()).isEqualTo("Violación a la integridad de la base de datos: fk");
        }
    }

    @Nested
    @DisplayName("addSubDiagnostic")
    class AddSubDiagnostic {

        @Test
        @DisplayName("IDs nulos: error")
        void idsNulos() {
            assertThat(service.addSubDiagnostic(null, 2).getMessage())
                    .isEqualTo("Los IDs del diagnóstico padre y del sub-diagnóstico no pueden ser nulos");
            assertThat(service.addSubDiagnostic(1, null).getMessage())
                    .isEqualTo("Los IDs del diagnóstico padre y del sub-diagnóstico no pueden ser nulos");
        }

        @Test
        @DisplayName("mismo ID: error")
        void mismoId() {
            assertThat(service.addSubDiagnostic(1, 1).getMessage()).isEqualTo("Un diagnóstico no puede ser sub-diagnóstico de sí mismo");
            verifyNoInteractions(medicalDiagnosticRepository);
        }

        @Test
        @DisplayName("padre inexistente: error")
        void padreInexistente() {
            when(medicalDiagnosticRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.addSubDiagnostic(1, 3).getMessage()).isEqualTo("Diagnóstico padre no encontrado con ID: 1");
        }

        @Test
        @DisplayName("sub-diagnóstico inexistente: error")
        void subInexistente() {
            when(medicalDiagnosticRepository.findById(1)).thenReturn(Optional.of(cardiopatiaIsquemica));
            when(medicalDiagnosticRepository.findById(3)).thenReturn(Optional.empty());

            assertThat(service.addSubDiagnostic(1, 3).getMessage()).isEqualTo("Sub-diagnóstico no encontrado con ID: 3");
        }

        @Test
        @DisplayName("sub-diagnóstico con otro padre: error sin guardar")
        void conOtroPadre() {
            MedicalDiagnostic otroPadre = MedicalDiagnostic.builder().id(5).diagnosticCode("I30").build();
            when(medicalDiagnosticRepository.findById(5)).thenReturn(Optional.of(otroPadre));
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.addSubDiagnostic(5, 2);

            assertThat(r.getMessage()).isEqualTo("Este sub-diagnóstico ya está asignado a otro diagnóstico padre");
            verify(medicalDiagnosticRepository, never()).save(any());
        }

        @Test
        @DisplayName("sub-diagnóstico raíz: se asigna al padre")
        void valido() {
            MedicalDiagnostic angina = MedicalDiagnostic.builder().id(3).diagnosticCode("I20.0").build();
            when(medicalDiagnosticRepository.findById(1)).thenReturn(Optional.of(cardiopatiaIsquemica));
            when(medicalDiagnosticRepository.findById(3)).thenReturn(Optional.of(angina));
            when(medicalDiagnosticRepository.save(angina)).thenReturn(angina);
            when(mapper.toResponseDTO(angina)).thenReturn(responseDTO);

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.addSubDiagnostic(1, 3);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Sub-diagnóstico agregado exitosamente al diagnóstico padre");
            assertThat(angina.getParentDiagnostic()).isSameAs(cardiopatiaIsquemica);
        }

        @Test
        @DisplayName("ya es hijo del mismo padre: se vuelve a guardar sin error")
        void yaEsHijo() {
            when(medicalDiagnosticRepository.findById(1)).thenReturn(Optional.of(cardiopatiaIsquemica));
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(medicalDiagnosticRepository.save(infarto)).thenReturn(infarto);

            assertThat(service.addSubDiagnostic(1, 2).isSuccess()).isTrue();
        }

        @Test
        @DisplayName("BUG CONOCIDO #18: permite ciclos (un ancestro pasa a ser hijo de su descendiente)")
        void permiteCiclos() {
            // I20 es padre de I21; se pide que I20 sea hijo de I21
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(medicalDiagnosticRepository.findById(1)).thenReturn(Optional.of(cardiopatiaIsquemica));
            // any(): con el ciclo, equals/hashCode de @Data entran en recursión infinita
            when(medicalDiagnosticRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.addSubDiagnostic(2, 1);

            assertThat(r.isSuccess()).isTrue();
            assertThat(cardiopatiaIsquemica.getParentDiagnostic() == infarto).isTrue();
            assertThat(infarto.getParentDiagnostic() == cardiopatiaIsquemica).isTrue();
        }
    }

    @Nested
    @DisplayName("removeSubDiagnostic")
    class RemoveSubDiagnostic {

        @Test
        @DisplayName("IDs nulos: error")
        void idsNulos() {
            assertThat(service.removeSubDiagnostic(null, 2).getMessage())
                    .isEqualTo("Los IDs del diagnóstico padre y del sub-diagnóstico no pueden ser nulos");
        }

        @Test
        @DisplayName("padre inexistente: error")
        void padreInexistente() {
            when(medicalDiagnosticRepository.existsById(1)).thenReturn(false);

            assertThat(service.removeSubDiagnostic(1, 2).getMessage()).isEqualTo("No se encontró un diagnóstico padre con ID: 1");
        }

        @Test
        @DisplayName("sub-diagnóstico inexistente: error")
        void subInexistente() {
            when(medicalDiagnosticRepository.existsById(1)).thenReturn(true);
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.empty());

            assertThat(service.removeSubDiagnostic(1, 2).getMessage()).isEqualTo("Sub-diagnóstico no encontrado con ID: 2");
        }

        @Test
        @DisplayName("no es hijo de ese padre: error sin guardar")
        void noEsHijo() {
            when(medicalDiagnosticRepository.existsById(5)).thenReturn(true);
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.removeSubDiagnostic(5, 2);

            assertThat(r.getMessage()).isEqualTo("Este sub-diagnóstico no pertenece al diagnóstico padre indicado");
            verify(medicalDiagnosticRepository, never()).save(any());
        }

        @Test
        @DisplayName("asignado a una categoría de dataset: error sin guardar")
        void enCategoria() {
            when(medicalDiagnosticRepository.existsById(1)).thenReturn(true);
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(categoryDiagnosticRepository.existsByMedicalDiagnosticId(2)).thenReturn(true);

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.removeSubDiagnostic(1, 2);

            assertThat(r.getMessage()).isEqualTo("No se puede remover este sub-diagnóstico porque está asignado a una categoría dentro de un dataset.");
            assertThat(infarto.getParentDiagnostic()).isSameAs(cardiopatiaIsquemica);
            verify(medicalDiagnosticRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: queda como diagnóstico raíz")
        void valido() {
            when(medicalDiagnosticRepository.existsById(1)).thenReturn(true);
            when(medicalDiagnosticRepository.findById(2)).thenReturn(Optional.of(infarto));
            when(categoryDiagnosticRepository.existsByMedicalDiagnosticId(anyInt())).thenReturn(false);
            when(medicalDiagnosticRepository.save(infarto)).thenReturn(infarto);
            when(mapper.toResponseDTO(infarto)).thenReturn(responseDTO);

            ApiResponse<MedicalDiagnosticResponseDTO> r = service.removeSubDiagnostic(1, 2);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Sub-diagnóstico removido del diagnóstico padre exitosamente");
            assertThat(infarto.getParentDiagnostic()).isNull();
        }
    }
}
