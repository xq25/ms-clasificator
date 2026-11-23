package backend.msclasificator.dataset.application;

import backend.msclasificator.dataset.api.dto.CategoryDiagnosticCreateDTO;
import backend.msclasificator.dataset.api.dto.CategoryDiagnosticResponseDTO;
import backend.msclasificator.dataset.api.dto.CategoryDiagnosticSummaryDTO;
import backend.msclasificator.dataset.api.mapper.CategoryDiagnosticMapper;
import backend.msclasificator.dataset.application.port.out.CategoryDiagnosticRepository;
import backend.msclasificator.dataset.application.port.out.DatasetCategoryRepository;
import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.dataset.domain.DatasetCategory;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryDiagnosticServiceTest {

    @Mock private CategoryDiagnosticRepository repository;
    @Mock private CategoryDiagnosticMapper mapper;
    @Mock private DatasetCategoryRepository datasetCategoryRepository;
    @Mock private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @InjectMocks private CategoryDiagnosticService service;

    private MedicalDiagnostic raiz;
    private MedicalDiagnostic subDiagnostico;
    private DatasetCategory categoria;
    private CategoryDiagnosticResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        raiz = MedicalDiagnostic.builder().id(31).diagnosticCode("C34").build();
        subDiagnostico = MedicalDiagnostic.builder().id(160).diagnosticCode("C34.1").parentDiagnostic(raiz).build();
        Dataset dataset = Dataset.builder().id(1).medicalDiagnostic(raiz).build();
        categoria = DatasetCategory.builder().id(100).name("Maligno").numValue(1).dataset(dataset).build();
        responseDTO = CategoryDiagnosticResponseDTO.builder().id(500).build();
    }

    @Nested
    @DisplayName("findAll / count / findById")
    class Consultas {

        @Test
        @DisplayName("findAll: construye el PagedResponse con el mapeo resumido")
        void findAll() {
            CategoryDiagnostic cd = CategoryDiagnostic.builder().id(500).build();
            CategoryDiagnosticSummaryDTO summary = CategoryDiagnosticSummaryDTO.builder().id(500).build();
            when(repository.findAll(new PageQuery(0, 10, "id", "asc")))
                    .thenReturn(new PageResult<>(List.of(cd), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(cd)).thenReturn(summary);

            ApiResponse<PagedResponse<CategoryDiagnosticSummaryDTO>> response = service.findAll(new PageRequestDTO());

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("DiagnosticCategoryDatasets obtenidos exitosamente");
            assertThat(response.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("count: devuelve el total")
        void count() {
            when(repository.count()).thenReturn(4L);

            assertThat(service.count().getData()).isEqualTo(4L);
        }

        @Test
        @DisplayName("findById inexistente: error")
        void findByIdInexistente() {
            when(repository.findById(500)).thenReturn(Optional.empty());

            assertThat(service.findById(500).getMessage()).isEqualTo("CategoryDiagnostic no encontrado con ID: 500");
        }
    }

    @Nested
    @DisplayName("findByDatasetCategoryId")
    class FindByDatasetCategoryId {

        @Test
        @DisplayName("categoría inexistente: error")
        void categoriaInexistente() {
            when(datasetCategoryRepository.existsById(100)).thenReturn(false);

            assertThat(service.findByDatasetCategoryId(100).getMessage())
                    .isEqualTo("DatasetCategory no encontrado con ID: 100");
        }

        @Test
        @DisplayName("categoría existente: éxito (mismo mensaje aunque la lista esté vacía)")
        void categoriaExistente() {
            when(datasetCategoryRepository.existsById(100)).thenReturn(true);
            when(repository.findByDatasetCategoryId(100)).thenReturn(List.of());

            ApiResponse<List<CategoryDiagnosticSummaryDTO>> response = service.findByDatasetCategoryId(100);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isEmpty();
            assertThat(response.getMessage()).isEqualTo("Diagnósticos obtenidos exitosamente");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private final CategoryDiagnosticCreateDTO dto = CategoryDiagnosticCreateDTO.builder()
                .datasetCategoryId(100).medicalDiagnosticId(160).build();

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("categoría inexistente: error")
        void categoriaInexistente() {
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.empty());

            assertThat(service.create(dto).getMessage()).isEqualTo("DatasetCategory no encontrado con ID: 100");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("diagnóstico inexistente: error")
        void diagnosticoInexistente() {
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.of(categoria));
            when(medicalDiagnosticRepository.findById(160)).thenReturn(Optional.empty());

            assertThat(service.create(dto).getMessage()).isEqualTo("MedicalDiagnostic no encontrado con ID: 160");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("diagnóstico ya asignado en otra categoría del mismo dataset: error")
        void yaAsignadoEnElDataset() {
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.of(categoria));
            when(medicalDiagnosticRepository.findById(160)).thenReturn(Optional.of(subDiagnostico));
            when(repository.existsByDatasetIdAndMedicalDiagnosticId(1, 160)).thenReturn(true);

            assertThat(service.create(dto).getMessage())
                    .isEqualTo("El diagnóstico seleccionado ya pertenece a otra categoría del mismo dataset");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("hijo directo de la raíz: asocia categoría y diagnóstico, y guarda")
        void valido() {
            CategoryDiagnostic nuevo = new CategoryDiagnostic();
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.of(categoria));
            when(medicalDiagnosticRepository.findById(160)).thenReturn(Optional.of(subDiagnostico));
            when(repository.existsByDatasetIdAndMedicalDiagnosticId(1, 160)).thenReturn(false);
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(repository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(responseDTO);

            ApiResponse<CategoryDiagnosticResponseDTO> response = service.create(dto);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Diagnóstico agregado exitosamente a la categoría");
            assertThat(nuevo.getDatasetCategory()).isSameAs(categoria);
            assertThat(nuevo.getMedicalDiagnostic()).isSameAs(subDiagnostico);
        }

        @Test
        @DisplayName("nieto de la raíz (cualquier profundidad): se acepta")
        void nietoAceptado() {
            MedicalDiagnostic nieto = MedicalDiagnostic.builder().id(900).diagnosticCode("C34.10").parentDiagnostic(subDiagnostico).build();
            CategoryDiagnosticCreateDTO dtoNieto = CategoryDiagnosticCreateDTO.builder()
                    .datasetCategoryId(100).medicalDiagnosticId(900).build();
            CategoryDiagnostic nuevo = new CategoryDiagnostic();
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.of(categoria));
            when(medicalDiagnosticRepository.findById(900)).thenReturn(Optional.of(nieto));
            when(repository.existsByDatasetIdAndMedicalDiagnosticId(1, 900)).thenReturn(false);
            when(mapper.toEntity(dtoNieto)).thenReturn(nuevo);
            when(repository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(responseDTO);

            ApiResponse<CategoryDiagnosticResponseDTO> response = service.create(dtoNieto);

            assertThat(response.isSuccess()).isTrue();
            assertThat(nuevo.getMedicalDiagnostic()).isSameAs(nieto);
        }

        @Test
        @DisplayName("diagnóstico fuera del árbol de la raíz: se rechaza y no guarda")
        void fueraDelArbolRechazado() {
            MedicalDiagnostic padreAjeno = MedicalDiagnostic.builder().id(57).diagnosticCode("J45").build();
            MedicalDiagnostic ajeno = MedicalDiagnostic.builder().id(230).diagnosticCode("J45.0").parentDiagnostic(padreAjeno).build();
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.of(categoria));
            when(medicalDiagnosticRepository.findById(230)).thenReturn(Optional.of(ajeno));

            ApiResponse<CategoryDiagnosticResponseDTO> response = service.create(CategoryDiagnosticCreateDTO.builder()
                    .datasetCategoryId(100).medicalDiagnosticId(230).build());

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage())
                    .isEqualTo("El diagnóstico seleccionado no pertenece al árbol de clasificación definido por el Dataset");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("la propia raíz del dataset: se rechaza (solo sub-diagnósticos)")
        void raizRechazada() {
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.of(categoria));
            when(medicalDiagnosticRepository.findById(31)).thenReturn(Optional.of(raiz));

            ApiResponse<CategoryDiagnosticResponseDTO> response = service.create(CategoryDiagnosticCreateDTO.builder()
                    .datasetCategoryId(100).medicalDiagnosticId(31).build());

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage())
                    .isEqualTo("El diagnóstico seleccionado no pertenece al árbol de clasificación definido por el Dataset");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("ciclo en parent_diagnostic_id que no pasa por la raíz: termina y rechaza")
        void cicloRechazado() {
            MedicalDiagnostic a = MedicalDiagnostic.builder().id(700).diagnosticCode("X1").build();
            MedicalDiagnostic b = MedicalDiagnostic.builder().id(701).diagnosticCode("X2").parentDiagnostic(a).build();
            a.setParentDiagnostic(b);
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.of(categoria));
            when(medicalDiagnosticRepository.findById(700)).thenReturn(Optional.of(a));

            ApiResponse<CategoryDiagnosticResponseDTO> response = service.create(CategoryDiagnosticCreateDTO.builder()
                    .datasetCategoryId(100).medicalDiagnosticId(700).build());

            assertThat(response.isSuccess()).isFalse();
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("inexistente: error y no borra")
        void inexistente() {
            when(repository.findById(500)).thenReturn(Optional.empty());

            assertThat(service.delete(500).getMessage()).isEqualTo("CategoryDiagnostic no encontrado con ID: 500");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("existente: la retira (soft delete) y queda en el historial de la definición")
        void existente() {
            CategoryDiagnostic cd = CategoryDiagnostic.builder().id(500).build();
            when(repository.findById(500)).thenReturn(Optional.of(cd));

            ApiResponse<Void> response = service.delete(500);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("CategoryDiagnostic eliminado exitosamente");
            assertThat(cd.getActive()).isFalse();
            assertThat(cd.getDeletedAt()).isNotNull();
            verify(repository).save(cd);
        }
    }
}
