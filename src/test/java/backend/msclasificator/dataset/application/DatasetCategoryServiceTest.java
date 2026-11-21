package backend.msclasificator.dataset.application;

import backend.msclasificator.dataset.api.dto.DatasetCategoryCreateDTO;
import backend.msclasificator.dataset.api.dto.DatasetCategoryResponseDTO;
import backend.msclasificator.dataset.api.dto.DatasetCategorySummaryDTO;
import backend.msclasificator.dataset.api.dto.DatasetCategoryUpdateDTO;
import backend.msclasificator.dataset.api.mapper.DatasetCategoryMapper;
import backend.msclasificator.dataset.application.port.out.DatasetCategoryRepository;
import backend.msclasificator.dataset.application.port.out.DatasetRepository;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.dataset.domain.DatasetCategory;
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
import org.mockito.ArgumentCaptor;
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
class DatasetCategoryServiceTest {

    @Mock private DatasetCategoryRepository datasetCategoryRepository;
    @Mock private DatasetCategoryMapper datasetCategoryMapper;
    @Mock private DatasetRepository datasetRepository;

    @InjectMocks private DatasetCategoryService service;

    private Dataset dataset;
    private DatasetCategoryResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        dataset = Dataset.builder().id(1).name("Rx tórax").build();
        responseDTO = DatasetCategoryResponseDTO.builder().id(100).name("Normal").numValue(1).build();
    }

    private DatasetCategory categoria(int id, int numValue) {
        return DatasetCategory.builder().id(id).name("Cat " + numValue).numValue(numValue).dataset(dataset).build();
    }

    @Nested
    @DisplayName("findAll / count / findById")
    class Consultas {

        @Test
        @DisplayName("findAll: construye el PagedResponse con el mapeo de respuesta")
        void findAll() {
            DatasetCategory cat = categoria(100, 1);
            when(datasetCategoryRepository.findAll(new PageQuery(0, 10, "id", "asc")))
                    .thenReturn(new PageResult<>(List.of(cat), 0, 10, 1, 1, true));
            when(datasetCategoryMapper.toResponseDTO(cat)).thenReturn(responseDTO);

            ApiResponse<PagedResponse<DatasetCategoryResponseDTO>> response = service.findAll(new PageRequestDTO());

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Categorias de dataset obtenidas exitosamente");
            assertThat(response.getData().getContent()).containsExactly(responseDTO);
            assertThat(response.getData().getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("count: devuelve el total")
        void count() {
            when(datasetCategoryRepository.count()).thenReturn(3L);

            ApiResponse<Long> response = service.count();

            assertThat(response.getData()).isEqualTo(3L);
            assertThat(response.getMessage()).isEqualTo("Total de categorías de dataset");
        }

        @Test
        @DisplayName("findById inexistente: error")
        void findByIdInexistente() {
            when(datasetCategoryRepository.findById(9)).thenReturn(Optional.empty());

            assertThat(service.findById(9).getMessage()).isEqualTo("Categoria no encontrada con ID: 9");
        }

        @Test
        @DisplayName("findById existente: devuelve la categoría")
        void findByIdExistente() {
            DatasetCategory cat = categoria(100, 1);
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.of(cat));
            when(datasetCategoryMapper.toResponseDTO(cat)).thenReturn(responseDTO);

            ApiResponse<DatasetCategoryResponseDTO> response = service.findById(100);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isSameAs(responseDTO);
            assertThat(response.getMessage()).isEqualTo("Categoria encontrada");
        }
    }

    @Nested
    @DisplayName("findByDatasetId")
    class FindByDatasetId {

        @Test
        @DisplayName("dataset inexistente: error")
        void datasetInexistente() {
            when(datasetRepository.existsById(1)).thenReturn(false);

            assertThat(service.findByDatasetId(1).getMessage()).isEqualTo("Dataset no encontrado con ID: 1");
        }

        @Test
        @DisplayName("sin categorías: éxito con lista vacía")
        void sinCategorias() {
            when(datasetRepository.existsById(1)).thenReturn(true);
            when(datasetCategoryRepository.findByDatasetId(1)).thenReturn(List.of());

            ApiResponse<List<DatasetCategorySummaryDTO>> response = service.findByDatasetId(1);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isEmpty();
            assertThat(response.getMessage()).isEqualTo("No se encontraron categorias para la configuración con ID: 1");
        }

        @Test
        @DisplayName("con categorías: éxito con la lista resumida")
        void conCategorias() {
            DatasetCategory cat = categoria(100, 1);
            DatasetCategorySummaryDTO summary = DatasetCategorySummaryDTO.builder().id(100).build();
            when(datasetRepository.existsById(1)).thenReturn(true);
            when(datasetCategoryRepository.findByDatasetId(1)).thenReturn(List.of(cat));
            when(datasetCategoryMapper.toSummaryDTO(cat)).thenReturn(summary);

            ApiResponse<List<DatasetCategorySummaryDTO>> response = service.findByDatasetId(1);

            assertThat(response.getData()).containsExactly(summary);
            assertThat(response.getMessage()).isEqualTo("Categorias encontradas para la configuración con ID: 1");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private final DatasetCategoryCreateDTO dto = DatasetCategoryCreateDTO.builder().name("Patológico").datasetId(1).build();

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("dataset inexistente: error y no guarda")
        void datasetInexistente() {
            when(datasetRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.create(dto).getMessage()).isEqualTo("Dataset no encontrada con ID: 1");
            verify(datasetCategoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: numValue = cantidad de categorías existentes + 1 y se asocia al dataset")
        void asignaSiguienteNumValue() {
            DatasetCategory nueva = DatasetCategory.builder().name("Patológico").build();
            when(datasetRepository.findById(1)).thenReturn(Optional.of(dataset));
            when(datasetCategoryMapper.toEntity(dto)).thenReturn(nueva);
            when(datasetCategoryRepository.findByDatasetId(1)).thenReturn(List.of(categoria(10, 1), categoria(11, 2)));
            when(datasetCategoryRepository.save(nueva)).thenReturn(nueva);
            when(datasetCategoryMapper.toResponseDTO(nueva)).thenReturn(responseDTO);

            ApiResponse<DatasetCategoryResponseDTO> response = service.create(dto);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Categoria de dataset creado exitosamente");
            assertThat(nueva.getNumValue()).isEqualTo(3);
            assertThat(nueva.getDataset()).isSameAs(dataset);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.update(100, null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("inexistente: error")
        void inexistente() {
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.empty());

            assertThat(service.update(100, new DatasetCategoryUpdateDTO("X")).getMessage())
                    .isEqualTo("Categoria de dataset no encontrada con ID: 100");
        }

        @Test
        @DisplayName("válido: solo cambia el nombre; numValue y dataset se conservan")
        void soloCambiaNombre() {
            DatasetCategory cat = categoria(100, 2);
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.of(cat));
            when(datasetCategoryRepository.save(cat)).thenReturn(cat);
            when(datasetCategoryMapper.toResponseDTO(cat)).thenReturn(responseDTO);

            ApiResponse<DatasetCategoryResponseDTO> response = service.update(100, new DatasetCategoryUpdateDTO("Renombrada"));

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Categoria de dataset actualizado exitosamente");
            ArgumentCaptor<DatasetCategory> guardada = ArgumentCaptor.forClass(DatasetCategory.class);
            verify(datasetCategoryRepository).save(guardada.capture());
            assertThat(guardada.getValue().getName()).isEqualTo("Renombrada");
            assertThat(guardada.getValue().getNumValue()).isEqualTo(2);
            assertThat(guardada.getValue().getDataset()).isSameAs(dataset);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("inexistente: error")
        void inexistente() {
            when(datasetCategoryRepository.findById(100)).thenReturn(Optional.empty());

            assertThat(service.delete(100).getMessage()).isEqualTo("Categoria de dataset no encontrada con ID: 100");
        }

        @Test
        @DisplayName("no es la última (numValue menor al máximo): rechaza y no borra")
        void noEsLaUltima() {
            DatasetCategory primera = categoria(10, 1);
            when(datasetCategoryRepository.findById(10)).thenReturn(Optional.of(primera));
            when(datasetCategoryRepository.findByDatasetId(1)).thenReturn(List.of(primera, categoria(11, 2)));

            ApiResponse<Void> response = service.delete(10);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage())
                    .isEqualTo("Solo se puede eliminar la ultima categoria generada de un Dataset, por coherencia secuencia de los numValues");
            verify(datasetCategoryRepository, never()).delete(any());
        }

        @Test
        @DisplayName("es la última: la borra")
        void esLaUltima() {
            DatasetCategory ultima = categoria(11, 2);
            when(datasetCategoryRepository.findById(11)).thenReturn(Optional.of(ultima));
            when(datasetCategoryRepository.findByDatasetId(1)).thenReturn(List.of(categoria(10, 1), ultima));

            ApiResponse<Void> response = service.delete(11);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Categoria de dataset eliminada exitosamente");
            verify(datasetCategoryRepository).delete(ultima);
        }
    }
}
