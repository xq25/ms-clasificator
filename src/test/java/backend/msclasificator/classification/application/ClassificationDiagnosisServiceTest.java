package backend.msclasificator.classification.application;

import backend.msclasificator.classification.api.dto.ClassificationDiagnosisCreateDTO;
import backend.msclasificator.classification.api.dto.ClassificationDiagnosisResponseDTO;
import backend.msclasificator.classification.api.mapper.ClassificationDiagnosisMapper;
import backend.msclasificator.classification.application.port.out.ClassificationDiagnosisRepository;
import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.classification.domain.ClassificationDiagnosis;
import backend.msclasificator.classification.domain.ImageClassification;
import backend.msclasificator.dataset.application.port.out.CategoryDiagnosticRepository;
import backend.msclasificator.dataset.application.port.out.DatasetRepository;
import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.diagnosiscatalog.application.port.out.MedicalDiagnosticRepository;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.medicalimage.domain.MedicalImage;
import backend.msclasificator.medicalimage.domain.MedicalImageType;
import backend.msclasificator.shared.api.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassificationDiagnosisServiceTest {

    @Mock private ClassificationDiagnosisRepository repository;
    @Mock private ClassificationDiagnosisMapper mapper;
    @Mock private ImageClassificationRepository imageClassificationRepository;
    @Mock private MedicalDiagnosticRepository medicalDiagnosticRepository;
    @Mock private DatasetRepository datasetRepository;
    @Mock private CategoryDiagnosticRepository categoryDiagnosticRepository;

    @InjectMocks private ClassificationDiagnosisService service;

    private static final LocalDateTime DESLIZADA = LocalDateTime.of(2026, 3, 1, 10, 0);

    // Dataset "Arritmias" (raíz I20) para el tipo Electrocardiograma (id 1), con categorías Negativo y Positivo
    private Dataset dataset;
    private DatasetCategory negativo;
    private DatasetCategory positivo;
    private ImageClassification clasificacion;
    private MedicalDiagnostic i203;
    private MedicalDiagnostic i204;
    private MedicalDiagnostic i201;

    @BeforeEach
    void setUp() {
        MedicalImageType ecg = MedicalImageType.builder().id(1).name("Electrocardiograma").build();
        MedicalImage imagen = MedicalImage.builder().id(50).medicalImageType(ecg).build();
        clasificacion = ImageClassification.builder().id(5).medicalImg(imagen).diagnosticDate(DESLIZADA).build();
        dataset = Dataset.builder().id(9).name("Arritmias").build();
        negativo = DatasetCategory.builder().id(30).name("Negativo").build();
        positivo = DatasetCategory.builder().id(31).name("Positivo").build();
        i201 = MedicalDiagnostic.builder().id(185).diagnosticCode("I20.1").build();
        i203 = MedicalDiagnostic.builder().id(300).diagnosticCode("I20.3").build();
        i204 = MedicalDiagnostic.builder().id(301).diagnosticCode("I20.4").build();
    }

    private ClassificationDiagnosisCreateDTO dto(int diagnosticId) {
        return ClassificationDiagnosisCreateDTO.builder().imageDiagnosticId(5).medicalDiagnosticId(diagnosticId).build();
    }

    private void categoria(MedicalDiagnostic diagnostico, DatasetCategory categoria) {
        when(categoryDiagnosticRepository.findByDatasetIdAndMedicalDiagnosticId(9, diagnostico.getId()))
                .thenReturn(Optional.ofNullable(categoria == null ? null : CategoryDiagnostic.builder().datasetCategory(categoria).build()));
    }

    private ClassificationDiagnosis asignado(MedicalDiagnostic diagnostico) {
        return ClassificationDiagnosis.builder().imageDiagnostic(clasificacion).medicalDiagnostic(diagnostico).build();
    }

    @Nested
    @DisplayName("create")
    class Create {

        private void stubBase(MedicalDiagnostic diagnostico) {
            when(imageClassificationRepository.findById(5)).thenReturn(Optional.of(clasificacion));
            when(medicalDiagnosticRepository.findById(diagnostico.getId())).thenReturn(Optional.of(diagnostico));
        }

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("clasificación inexistente: error")
        void clasificacionInexistente() {
            when(imageClassificationRepository.findById(5)).thenReturn(Optional.empty());

            assertThat(service.create(dto(300)).getMessage()).isEqualTo("ImageClassification no encontrado con ID: 5");
        }

        @Test
        @DisplayName("diagnóstico ya asignado a la imagen: error")
        void duplicado() {
            stubBase(i203);
            when(repository.existsByImageClassificationIdAndMedicalDiagnosticId(5, 300)).thenReturn(true);

            assertThat(service.create(dto(300)).getMessage()).isEqualTo("Este diagnóstico ya fue asignado a la imagen");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("tipo de imagen sin dataset (clasificación previa a la regla): error")
        void sinDataset() {
            stubBase(i203);
            when(datasetRepository.findByMedicalImageTypeId(1)).thenReturn(Optional.empty());

            assertThat(service.create(dto(300)).getMessage()).isEqualTo("El tipo de imagen médica no tiene un dataset configurado");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("diagnóstico que no está en ninguna categoría del dataset: error")
        void sinCategoria() {
            stubBase(i201);
            when(datasetRepository.findByMedicalImageTypeId(1)).thenReturn(Optional.of(dataset));
            categoria(i201, null);

            assertThat(service.create(dto(185)).getMessage())
                    .isEqualTo("El diagnóstico no pertenece a ninguna categoría del dataset 'Arritmias'");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("primer diagnóstico de la clasificación, con categoría: se asigna")
        void primerDiagnostico() {
            ClassificationDiagnosis nuevo = new ClassificationDiagnosis();
            ClassificationDiagnosisResponseDTO respuesta = new ClassificationDiagnosisResponseDTO();
            stubBase(i203);
            when(datasetRepository.findByMedicalImageTypeId(1)).thenReturn(Optional.of(dataset));
            categoria(i203, negativo);
            when(repository.findByImageClassificationId(5)).thenReturn(List.of());
            when(mapper.toEntity(dto(300))).thenReturn(nuevo);
            when(repository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(respuesta);

            ApiResponse<ClassificationDiagnosisResponseDTO> response = service.create(dto(300));

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Diagnóstico asignado exitosamente");
            assertThat(nuevo.getImageDiagnostic()).isSameAs(clasificacion);
            assertThat(nuevo.getMedicalDiagnostic()).isSameAs(i203);
        }

        @Test
        @DisplayName("misma categoría que los ya asignados: se asigna")
        void mismaCategoria() {
            MedicalDiagnostic i205 = MedicalDiagnostic.builder().id(302).diagnosticCode("I20.5").build();
            ClassificationDiagnosis nuevo = new ClassificationDiagnosis();
            stubBase(i205);
            when(datasetRepository.findByMedicalImageTypeId(1)).thenReturn(Optional.of(dataset));
            categoria(i205, negativo);
            categoria(i203, negativo);
            when(repository.findByImageClassificationId(5)).thenReturn(List.of(asignado(i203)));
            when(mapper.toEntity(dto(302))).thenReturn(nuevo);
            when(repository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(new ClassificationDiagnosisResponseDTO());

            assertThat(service.create(dto(302)).isSuccess()).isTrue();
            verify(repository).save(nuevo);
        }

        @Test
        @DisplayName("categoría distinta a la de los ya asignados: error y no guarda")
        void otraCategoria() {
            stubBase(i204);
            when(datasetRepository.findByMedicalImageTypeId(1)).thenReturn(Optional.of(dataset));
            categoria(i204, positivo);
            categoria(i203, negativo);
            when(repository.findByImageClassificationId(5)).thenReturn(List.of(asignado(i203)));

            ApiResponse<ClassificationDiagnosisResponseDTO> response = service.create(dto(301));

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage())
                    .isEqualTo("La imagen ya está clasificada en la categoría 'Negativo'; todos sus diagnósticos deben ser de la misma categoría");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("clasificación con categoría: rechaza un diagnóstico de otra categoría")
        void detalleFueraDeLaCategoriaDeLaClasificacion() {
            clasificacion.setDatasetCategory(negativo);
            stubBase(i204);
            when(categoryDiagnosticRepository.findDefinitionAt(30, DESLIZADA))
                    .thenReturn(List.of(CategoryDiagnostic.builder().medicalDiagnostic(i203).build()));

            assertThat(service.create(dto(301)).getMessage())
                    .isEqualTo("El diagnóstico no pertenece a la categoría 'Negativo' de la clasificación");
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("clasificación con categoría: acepta un sub-diagnóstico de esa categoría como detalle")
        void detalleDentroDeLaCategoriaDeLaClasificacion() {
            clasificacion.setDatasetCategory(negativo);
            ClassificationDiagnosis nuevo = new ClassificationDiagnosis();
            stubBase(i203);
            when(categoryDiagnosticRepository.findDefinitionAt(30, DESLIZADA))
                    .thenReturn(List.of(CategoryDiagnostic.builder().medicalDiagnostic(i203).build()));
            when(mapper.toEntity(dto(300))).thenReturn(nuevo);
            when(repository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(new ClassificationDiagnosisResponseDTO());

            assertThat(service.create(dto(300)).isSuccess()).isTrue();
        }

        @Test
        @DisplayName("clasificación con categoría: un diagnóstico retirado de la categoría DESPUÉS de deslizar sigue siendo válido")
        void detalleConDefinicionHistorica() {
            // La definición vigente al deslizar incluía I20.4; hoy ya no (se valida contra la fecha de la clasificación)
            clasificacion.setDatasetCategory(positivo);
            ClassificationDiagnosis nuevo = new ClassificationDiagnosis();
            stubBase(i204);
            when(categoryDiagnosticRepository.findDefinitionAt(31, DESLIZADA))
                    .thenReturn(List.of(CategoryDiagnostic.builder().medicalDiagnostic(i204).build()));
            when(mapper.toEntity(dto(301))).thenReturn(nuevo);
            when(repository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(new ClassificationDiagnosisResponseDTO());

            assertThat(service.create(dto(301)).isSuccess()).isTrue();
            verify(datasetRepository, never()).findByMedicalImageTypeId(any());
        }

        @Test
        @DisplayName("ya tiene un diagnóstico sin categoría (dato previo a la regla): error y no guarda")
        void existenteSinCategoria() {
            stubBase(i203);
            when(datasetRepository.findByMedicalImageTypeId(1)).thenReturn(Optional.of(dataset));
            categoria(i203, negativo);
            categoria(i201, null);
            when(repository.findByImageClassificationId(5)).thenReturn(List.of(asignado(i201)));

            assertThat(service.create(dto(300)).getMessage())
                    .isEqualTo("La imagen tiene diagnósticos que no pertenecen a ninguna categoría del dataset; no se pueden agregar más");
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete / count")
    class DeleteCount {

        @Test
        @DisplayName("delete inexistente: error")
        void deleteInexistente() {
            when(repository.findById(7)).thenReturn(Optional.empty());

            assertThat(service.delete(7).getMessage()).isEqualTo("Diagnóstico de imagen no encontrado con ID: 7");
            verify(repository, never()).delete(any());
        }

        @Test
        @DisplayName("delete existente: borra")
        void deleteExistente() {
            ClassificationDiagnosis cd = asignado(i203);
            when(repository.findById(7)).thenReturn(Optional.of(cd));

            assertThat(service.delete(7).getMessage()).isEqualTo("Diagnóstico eliminado exitosamente");
            verify(repository).delete(cd);
        }

        @Test
        @DisplayName("count: devuelve el total")
        void count() {
            when(repository.count()).thenReturn(7L);

            assertThat(service.count().getData()).isEqualTo(7L);
        }
    }
}
