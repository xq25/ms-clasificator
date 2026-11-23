package backend.msclasificator.dataset.application;

import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.dataset.api.dto.DatasetCreateDTO;
import backend.msclasificator.dataset.api.dto.DatasetResponseDTO;
import backend.msclasificator.dataset.api.dto.DatasetUpdateDTO;
import backend.msclasificator.dataset.api.mapper.DatasetMapper;
import backend.msclasificator.dataset.application.port.out.DatasetCategoryRepository;
import backend.msclasificator.dataset.application.port.out.DatasetRepository;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.diagnosiscatalog.application.port.out.MedicalDiagnosticRepository;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageTypeRepository;
import backend.msclasificator.medicalimage.domain.MedicalImageType;
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
class DatasetServiceTest {

    @Mock private DatasetRepository datasetRepository;
    @Mock private DatasetMapper datasetMapper;
    @Mock private MedicalDiagnosticRepository medicalDiagnosticRepository;
    @Mock private MedicalImageTypeRepository medicalImageTypeRepository;
    @Mock private DatasetCategoryRepository datasetCategoryRepository;
    @Mock private ImageClassificationRepository imageClassificationRepository;

    @InjectMocks private DatasetService service;

    private Dataset dataset;
    private MedicalDiagnostic diagnostic;
    private MedicalImageType imageType;
    private DatasetResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        diagnostic = MedicalDiagnostic.builder().id(10).diagnosticCode("J18").build();
        imageType = MedicalImageType.builder().id(5).name("Radiografía de Tórax").build();
        dataset = Dataset.builder().id(1).name("Rx tórax").medicalDiagnostic(diagnostic).medicalImageType(imageType).build();
        responseDTO = DatasetResponseDTO.builder().id(1).name("Rx tórax").build();
    }

    @Nested
    @DisplayName("findAll / count")
    class Listado {

        @Test
        @DisplayName("findAll: traduce PageRequestDTO a PageQuery y construye el PagedResponse")
        void findAll_construyePagedResponse() {
            PageRequestDTO request = new PageRequestDTO();
            when(datasetRepository.findAll(new PageQuery(0, 10, "id", "asc")))
                    .thenReturn(new PageResult<>(List.of(dataset), 0, 10, 1, 1, true));
            when(datasetMapper.toResponseDTO(dataset)).thenReturn(responseDTO);

            ApiResponse<PagedResponse<DatasetResponseDTO>> response = service.findAll(request);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Datasets obtenidos exitosamente");
            PagedResponse<DatasetResponseDTO> page = response.getData();
            assertThat(page.getContent()).containsExactly(responseDTO);
            assertThat(page.getPage()).isZero();
            assertThat(page.getSize()).isEqualTo(10);
            assertThat(page.getTotalElements()).isEqualTo(1);
            assertThat(page.getTotalPages()).isEqualTo(1);
            assertThat(page.isLast()).isTrue();
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count_devuelveTotal() {
            when(datasetRepository.count()).thenReturn(7L);

            ApiResponse<Long> response = service.count();

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isEqualTo(7L);
            assertThat(response.getMessage()).isEqualTo("Total de Datasets");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("existe: devuelve el dataset mapeado")
        void existe() {
            when(datasetRepository.findById(1)).thenReturn(Optional.of(dataset));
            when(datasetMapper.toResponseDTO(dataset)).thenReturn(responseDTO);

            ApiResponse<DatasetResponseDTO> response = service.findById(1);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isSameAs(responseDTO);
            assertThat(response.getMessage()).isEqualTo("Dataset encontrado");
        }

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(datasetRepository.findById(99)).thenReturn(Optional.empty());

            ApiResponse<DatasetResponseDTO> response = service.findById(99);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Dataset no encontrado con ID: 99");
        }
    }

    @Nested
    @DisplayName("findByMedicalImageTypeId")
    class FindByMedicalImageTypeId {

        @Test
        @DisplayName("tipo de imagen inexistente: error sin consultar datasets")
        void tipoInexistente() {
            when(medicalImageTypeRepository.existsById(5)).thenReturn(false);

            ApiResponse<DatasetResponseDTO> response = service.findByMedicalImageTypeId(5);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Tipo de imagen medica no encontrada con ID: 5");
            verify(datasetRepository, never()).findByMedicalImageTypeId(any());
        }

        @Test
        @DisplayName("tipo sin dataset: error")
        void tipoSinDataset() {
            when(medicalImageTypeRepository.existsById(5)).thenReturn(true);
            when(datasetRepository.findByMedicalImageTypeId(5)).thenReturn(Optional.empty());

            ApiResponse<DatasetResponseDTO> response = service.findByMedicalImageTypeId(5);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage())
                    .isEqualTo("No se encontró un Dataset asignado a clasificar el tipo de imagen medica con ID: 5");
        }

        @Test
        @DisplayName("tipo con dataset: lo devuelve")
        void tipoConDataset() {
            when(medicalImageTypeRepository.existsById(5)).thenReturn(true);
            when(datasetRepository.findByMedicalImageTypeId(5)).thenReturn(Optional.of(dataset));
            when(datasetMapper.toResponseDTO(dataset)).thenReturn(responseDTO);

            ApiResponse<DatasetResponseDTO> response = service.findByMedicalImageTypeId(5);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isSameAs(responseDTO);
            assertThat(response.getMessage()).isEqualTo("Dataset encontrado para el tipo de imagen medica con ID: 5");
        }
    }

    @Nested
    @DisplayName("findByMedicalDiagnosticId")
    class FindByMedicalDiagnosticId {

        @Test
        @DisplayName("diagnóstico inexistente: error")
        void diagnosticoInexistente() {
            when(medicalDiagnosticRepository.existsById(10)).thenReturn(false);

            ApiResponse<List<DatasetResponseDTO>> response = service.findByMedicalDiagnosticId(10);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Diagnóstico médico no encontrado con ID: 10");
        }

        @Test
        @DisplayName("sin datasets: éxito con lista vacía y mensaje específico")
        void sinDatasets() {
            when(medicalDiagnosticRepository.existsById(10)).thenReturn(true);
            when(datasetRepository.findByMedicalDiagnosticId(10)).thenReturn(List.of());

            ApiResponse<List<DatasetResponseDTO>> response = service.findByMedicalDiagnosticId(10);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isEmpty();
            assertThat(response.getMessage())
                    .isEqualTo("No se encontraron Datasets para clasificar el diagnóstico médico con ID: 10");
        }

        @Test
        @DisplayName("con datasets: éxito con la lista mapeada")
        void conDatasets() {
            when(medicalDiagnosticRepository.existsById(10)).thenReturn(true);
            when(datasetRepository.findByMedicalDiagnosticId(10)).thenReturn(List.of(dataset));
            when(datasetMapper.toResponseDTO(dataset)).thenReturn(responseDTO);

            ApiResponse<List<DatasetResponseDTO>> response = service.findByMedicalDiagnosticId(10);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).containsExactly(responseDTO);
            assertThat(response.getMessage()).isEqualTo("Datasets encontrados para el diagnóstico médico con ID: 10");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private final DatasetCreateDTO dto = DatasetCreateDTO.builder()
                .name("Rx tórax").medicalDiagnosticId(10).medicalImageTypeId(5).build();

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            ApiResponse<DatasetResponseDTO> response = service.create(null);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("diagnóstico inexistente: error")
        void diagnosticoInexistente() {
            when(medicalDiagnosticRepository.findById(10)).thenReturn(Optional.empty());

            ApiResponse<DatasetResponseDTO> response = service.create(dto);

            assertThat(response.getMessage()).isEqualTo("Diagnóstico médico no encontrado con ID: 10");
            verify(datasetRepository, never()).save(any());
        }

        @Test
        @DisplayName("tipo de imagen inexistente: error")
        void tipoInexistente() {
            when(medicalDiagnosticRepository.findById(10)).thenReturn(Optional.of(diagnostic));
            when(medicalImageTypeRepository.findById(5)).thenReturn(Optional.empty());

            ApiResponse<DatasetResponseDTO> response = service.create(dto);

            assertThat(response.getMessage()).isEqualTo("Tipo de imagen médica no encontrado con ID: 5");
            verify(datasetRepository, never()).save(any());
        }

        @Test
        @DisplayName("nombre ya usado por otro dataset: error y no guarda")
        void nombreDuplicado() {
            when(medicalDiagnosticRepository.findById(10)).thenReturn(Optional.of(diagnostic));
            when(medicalImageTypeRepository.findById(5)).thenReturn(Optional.of(imageType));
            when(datasetRepository.existsByName("Rx tórax")).thenReturn(true);

            ApiResponse<DatasetResponseDTO> response = service.create(dto);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Ya existe un dataset con el nombre: Rx tórax");
            verify(datasetRepository, never()).save(any());
        }

        @Test
        @DisplayName("ya existe dataset para el diagnóstico: error")
        void diagnosticoDuplicado() {
            when(medicalDiagnosticRepository.findById(10)).thenReturn(Optional.of(diagnostic));
            when(medicalImageTypeRepository.findById(5)).thenReturn(Optional.of(imageType));
            when(datasetRepository.existsByMedicalDiagnosticId(10)).thenReturn(true);

            ApiResponse<DatasetResponseDTO> response = service.create(dto);

            assertThat(response.getMessage()).isEqualTo("Ya existe una dataset para este diagnóstico médico");
            verify(datasetRepository, never()).save(any());
        }

        @Test
        @DisplayName("ya existe dataset para el tipo de imagen: error")
        void tipoDuplicado() {
            when(medicalDiagnosticRepository.findById(10)).thenReturn(Optional.of(diagnostic));
            when(medicalImageTypeRepository.findById(5)).thenReturn(Optional.of(imageType));
            when(datasetRepository.existsByMedicalDiagnosticId(10)).thenReturn(false);
            when(datasetRepository.existsByMedicalImageTypeId(5)).thenReturn(true);

            ApiResponse<DatasetResponseDTO> response = service.create(dto);

            assertThat(response.getMessage()).isEqualTo("Ya existe una dataset para clasificar este tipo de imagen médica");
            verify(datasetRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: asigna diagnóstico y tipo, y guarda")
        void valido() {
            Dataset nuevo = Dataset.builder().name("Rx tórax").build();
            when(medicalDiagnosticRepository.findById(10)).thenReturn(Optional.of(diagnostic));
            when(medicalImageTypeRepository.findById(5)).thenReturn(Optional.of(imageType));
            when(datasetRepository.existsByMedicalDiagnosticId(10)).thenReturn(false);
            when(datasetRepository.existsByMedicalImageTypeId(5)).thenReturn(false);
            when(datasetMapper.toEntity(dto)).thenReturn(nuevo);
            when(datasetRepository.save(nuevo)).thenReturn(nuevo);
            when(datasetMapper.toResponseDTO(nuevo)).thenReturn(responseDTO);

            ApiResponse<DatasetResponseDTO> response = service.create(dto);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Dataset creado exitosamente");
            assertThat(nuevo.getMedicalDiagnostic()).isSameAs(diagnostic);
            assertThat(nuevo.getMedicalImageType()).isSameAs(imageType);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private final MedicalDiagnostic otroDiagnostic = MedicalDiagnostic.builder().id(20).diagnosticCode("I20").build();
        private final MedicalImageType otroTipo = MedicalImageType.builder().id(6).name("Ecocardiograma").build();

        private DatasetUpdateDTO dto(String name, int diagnosticId, int imageTypeId) {
            return DatasetUpdateDTO.builder().name(name).medicalDiagnosticId(diagnosticId).medicalImageTypeId(imageTypeId).build();
        }

        private void stubEntidades(MedicalDiagnostic diag, MedicalImageType tipo) {
            when(datasetRepository.findById(1)).thenReturn(Optional.of(dataset));
            when(medicalDiagnosticRepository.findById(diag.getId())).thenReturn(Optional.of(diag));
            when(medicalImageTypeRepository.findById(tipo.getId())).thenReturn(Optional.of(tipo));
        }

        private Dataset capturarGuardado() {
            ArgumentCaptor<Dataset> guardado = ArgumentCaptor.forClass(Dataset.class);
            verify(datasetRepository).save(guardado.capture());
            return guardado.getValue();
        }

        private void stubGuardado() {
            when(datasetRepository.save(dataset)).thenReturn(dataset);
            when(datasetMapper.toResponseDTO(dataset)).thenReturn(responseDTO);
        }

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.update(1, null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("dataset inexistente: error")
        void datasetInexistente() {
            when(datasetRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.update(1, dto("X", 10, 5)).getMessage()).isEqualTo("Dataset no encontrado con ID: 1");
        }

        @Test
        @DisplayName("mismo diagnóstico y tipo, nombre nuevo: acepta, guarda el nombre y no consulta categorías")
        void renombrarConservandoDiagnostico() {
            stubEntidades(diagnostic, imageType);
            stubGuardado();

            ApiResponse<DatasetResponseDTO> response = service.update(1, dto("Nombre nuevo", 10, 5));

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Dataset actualizado exitosamente");
            Dataset guardado = capturarGuardado();
            assertThat(guardado.getName()).isEqualTo("Nombre nuevo");
            assertThat(guardado.getMedicalDiagnostic()).isSameAs(diagnostic);
            assertThat(guardado.getMedicalImageType()).isSameAs(imageType);
            verify(datasetCategoryRepository, never()).findByDatasetId(any());
        }

        @Test
        @DisplayName("nombre usado por otro dataset: error y no guarda")
        void nombreDuplicado() {
            stubEntidades(diagnostic, imageType);
            when(datasetRepository.existsByNameAndIdNot("Ocupado", 1)).thenReturn(true);

            ApiResponse<DatasetResponseDTO> response = service.update(1, dto("Ocupado", 10, 5));

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Ya existe un dataset con el nombre: Ocupado");
            verify(datasetRepository, never()).save(any());
        }

        @Test
        @DisplayName("diagnóstico usado por otro dataset: error y no guarda")
        void diagnosticoDeOtroDataset() {
            stubEntidades(otroDiagnostic, imageType);
            when(datasetRepository.existsByMedicalDiagnosticIdAndIdNot(20, 1)).thenReturn(true);

            ApiResponse<DatasetResponseDTO> response = service.update(1, dto("Rx tórax", 20, 5));

            assertThat(response.getMessage()).isEqualTo("Ya existe un Dataset para clasificar este diagnóstico médico");
            verify(datasetRepository, never()).save(any());
        }

        @Test
        @DisplayName("tipo de imagen usado por otro dataset: error y no guarda")
        void tipoDeOtroDataset() {
            stubEntidades(diagnostic, otroTipo);
            when(datasetRepository.existsByMedicalImageTypeIdAndIdNot(6, 1)).thenReturn(true);

            ApiResponse<DatasetResponseDTO> response = service.update(1, dto("Rx tórax", 10, 6));

            assertThat(response.getMessage()).isEqualTo("Ya existe un Dataset para clasificar este tipo de imagen médica");
            verify(datasetRepository, never()).save(any());
        }

        @Test
        @DisplayName("cambia el diagnóstico con categorías existentes: error y no guarda")
        void cambiaDiagnosticoConCategorias() {
            stubEntidades(otroDiagnostic, imageType);
            when(datasetCategoryRepository.findByDatasetId(1)).thenReturn(List.of(new DatasetCategory()));

            ApiResponse<DatasetResponseDTO> response = service.update(1, dto("Rx tórax", 20, 5));

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).startsWith("No se puede cambiar el diagnóstico médico de un Dataset que ya tiene categorías");
            verify(datasetRepository, never()).save(any());
        }

        @Test
        @DisplayName("cambia el diagnóstico sin categorías: acepta")
        void cambiaDiagnosticoSinCategorias() {
            stubEntidades(otroDiagnostic, imageType);
            when(datasetCategoryRepository.findByDatasetId(1)).thenReturn(List.of());
            stubGuardado();

            ApiResponse<DatasetResponseDTO> response = service.update(1, dto("Rx tórax", 20, 5));

            assertThat(response.isSuccess()).isTrue();
            assertThat(capturarGuardado().getMedicalDiagnostic()).isSameAs(otroDiagnostic);
        }

        @Test
        @DisplayName("con categorías, mismo diagnóstico y otro tipo de imagen: acepta")
        void cambiaTipoConCategorias() {
            stubEntidades(diagnostic, otroTipo);
            stubGuardado();

            ApiResponse<DatasetResponseDTO> response = service.update(1, dto("Rx tórax", 10, 6));

            assertThat(response.isSuccess()).isTrue();
            assertThat(capturarGuardado().getMedicalImageType()).isSameAs(otroTipo);
            verify(datasetCategoryRepository, never()).findByDatasetId(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("inexistente: error y no borra")
        void inexistente() {
            when(datasetRepository.findById(1)).thenReturn(Optional.empty());

            ApiResponse<Void> response = service.delete(1);

            assertThat(response.getMessage()).isEqualTo("Dataset no encontrada con ID: 1");
            verify(datasetRepository, never()).delete(any());
        }

        @Test
        @DisplayName("con imágenes clasificadas en sus categorías: error y no borra")
        void conClasificaciones() {
            when(datasetRepository.findById(1)).thenReturn(Optional.of(dataset));
            when(imageClassificationRepository.existsByDatasetCategoryDatasetId(1)).thenReturn(true);

            ApiResponse<Void> response = service.delete(1);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("No se puede eliminar el dataset: tiene imágenes clasificadas en sus categorías");
            verify(datasetRepository, never()).delete(any());
        }

        @Test
        @DisplayName("existente sin clasificaciones: borra (las categorías caen por cascada)")
        void existente() {
            when(datasetRepository.findById(1)).thenReturn(Optional.of(dataset));

            ApiResponse<Void> response = service.delete(1);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Dataset eliminada exitosamente (y sus categories asociados)");
            verify(datasetRepository).delete(dataset);
        }
    }
}
