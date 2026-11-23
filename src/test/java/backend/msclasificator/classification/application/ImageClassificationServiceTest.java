package backend.msclasificator.classification.application;

import backend.msclasificator.classification.api.dto.ImageClassificationCreateDTO;
import backend.msclasificator.classification.api.dto.ImageClassificationResponseDTO;
import backend.msclasificator.classification.api.mapper.ImageClassificationMapper;
import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.classification.domain.ImageClassification;
import backend.msclasificator.dataset.application.port.out.CategoryDiagnosticRepository;
import backend.msclasificator.dataset.application.port.out.DatasetCategoryRepository;
import backend.msclasificator.dataset.application.port.out.DatasetRepository;
import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
import backend.msclasificator.diagnosiscatalog.api.mapper.MedicalDiagnosticMapper;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.doctor.application.port.out.DoctorAreaRepository;
import backend.msclasificator.doctor.application.port.out.DoctorRepository;
import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.doctor.domain.EvaluationArea;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Cubre create (deslizamiento, ADR-010) y findById; el resto de ImageClassificationService queda pendiente (ADR-008)
@ExtendWith(MockitoExtension.class)
class ImageClassificationServiceTest {

    @Mock private ImageClassificationRepository imageClassificationRepository;
    @Mock private ImageClassificationMapper mapper;
    @Mock private DoctorRepository doctorRepository;
    @Mock private MedicalImageRepository medicalImageRepository;
    @Mock private DoctorAreaRepository doctorAreaRepository;
    @Mock private DatasetRepository datasetRepository;
    @Mock private DatasetCategoryRepository datasetCategoryRepository;
    @Mock private CategoryDiagnosticRepository categoryDiagnosticRepository;
    @Mock private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @InjectMocks private ImageClassificationService service;

    private Doctor doctor;
    private MedicalImage imagen;
    private Dataset dataset;
    private DatasetCategory positivo;
    private final ImageClassificationCreateDTO dto = ImageClassificationCreateDTO.builder()
            .doctorId(2).medicalImgId(50).datasetCategoryId(31).build();

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder().id(2).code("MED-01").build();
        EvaluationArea cardio = EvaluationArea.builder().id(1).codeArea("CARDIO").build();
        MedicalImageType ecg = MedicalImageType.builder().id(1).name("Electrocardiograma").evaluationArea(cardio).build();
        imagen = MedicalImage.builder().id(50).medicalImageType(ecg).build();
        dataset = Dataset.builder().id(9).name("Arritmias").build();
        positivo = DatasetCategory.builder().id(31).name("Positivo").numValue(3).dataset(dataset).build();
    }

    private void stubHastaAutorizacion() {
        when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
        when(medicalImageRepository.findById(50)).thenReturn(Optional.of(imagen));
        when(datasetRepository.findByMedicalImageTypeId(1)).thenReturn(Optional.of(dataset));
        when(doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(true);
    }

    @Nested
    @DisplayName("create (deslizamiento)")
    class Create {

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("tipo de imagen sin dataset: error y no guarda")
        void sinDataset() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(medicalImageRepository.findById(50)).thenReturn(Optional.of(imagen));
            when(datasetRepository.findByMedicalImageTypeId(1)).thenReturn(Optional.empty());

            ApiResponse<ImageClassificationResponseDTO> response = service.create(dto);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("El tipo de imagen médica no tiene un dataset configurado; no se puede clasificar");
            verify(imageClassificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("tipo de imagen sin área de evaluación: error")
        void sinArea() {
            imagen.getMedicalImageType().setEvaluationArea(null);
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(medicalImageRepository.findById(50)).thenReturn(Optional.of(imagen));
            when(datasetRepository.findByMedicalImageTypeId(1)).thenReturn(Optional.of(dataset));

            assertThat(service.create(dto).getMessage())
                    .startsWith("El tipo de imagen médica no tiene un área de evaluación asignada");
            verify(imageClassificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("doctor fuera del área del tipo de imagen: error")
        void doctorSinAutorizacion() {
            when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
            when(medicalImageRepository.findById(50)).thenReturn(Optional.of(imagen));
            when(datasetRepository.findByMedicalImageTypeId(1)).thenReturn(Optional.of(dataset));
            when(doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(false);

            assertThat(service.create(dto).getMessage()).isEqualTo("El doctor no tiene autorización para diagnosticar ese tipo de imagen médica");
            verify(imageClassificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("categoría inexistente: error")
        void categoriaInexistente() {
            stubHastaAutorizacion();
            when(datasetCategoryRepository.findById(31)).thenReturn(Optional.empty());

            assertThat(service.create(dto).getMessage()).isEqualTo("Categoría no encontrada con ID: 31");
            verify(imageClassificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("categoría de otro dataset: error y no guarda")
        void categoriaDeOtroDataset() {
            DatasetCategory ajena = DatasetCategory.builder().id(31).name("Maligno")
                    .dataset(Dataset.builder().id(99).name("Pulmón").build()).build();
            stubHastaAutorizacion();
            when(datasetCategoryRepository.findById(31)).thenReturn(Optional.of(ajena));

            assertThat(service.create(dto).getMessage())
                    .isEqualTo("La categoría no pertenece al dataset 'Arritmias' del tipo de imagen");
            verify(imageClassificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("el doctor ya clasificó esa imagen: error")
        void duplicado() {
            stubHastaAutorizacion();
            when(datasetCategoryRepository.findById(31)).thenReturn(Optional.of(positivo));
            when(imageClassificationRepository.findByDoctorIdAndMedicalImageId(2, 50))
                    .thenReturn(Optional.of(new ImageClassification()));

            assertThat(service.create(dto).getMessage()).isEqualTo("Ya existe un diagnóstico dado por ese doctor a esa imagen médica");
            verify(imageClassificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: guarda doctor, imagen, fecha y categoría; responde con los diagnósticos de la categoría")
        void valido() {
            MedicalDiagnostic i204 = MedicalDiagnostic.builder().id(301).diagnosticCode("I20.4").build();
            MedicalDiagnosticSummaryDTO i204Dto = MedicalDiagnosticSummaryDTO.builder().id(301).diagnosticCode("I20.4").build();
            ImageClassification nueva = new ImageClassification();
            stubHastaAutorizacion();
            when(datasetCategoryRepository.findById(31)).thenReturn(Optional.of(positivo));
            when(imageClassificationRepository.findByDoctorIdAndMedicalImageId(2, 50)).thenReturn(Optional.empty());
            when(mapper.toEntity(dto)).thenReturn(nueva);
            when(imageClassificationRepository.save(nueva)).thenReturn(nueva);
            when(mapper.toResponseDTO(nueva)).thenReturn(new ImageClassificationResponseDTO());
            when(categoryDiagnosticRepository.findDefinitionAt(eq(31), any(LocalDateTime.class)))
                    .thenReturn(List.of(CategoryDiagnostic.builder().medicalDiagnostic(i204).build()));
            when(medicalDiagnosticMapper.toSummaryDTO(i204)).thenReturn(i204Dto);

            ApiResponse<ImageClassificationResponseDTO> response = service.create(dto);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Diagnóstico de imagen creado exitosamente");
            assertThat(nueva.getDoctor()).isSameAs(doctor);
            assertThat(nueva.getMedicalImg()).isSameAs(imagen);
            assertThat(nueva.getDatasetCategory()).isSameAs(positivo);
            assertThat(nueva.getDiagnosticDate()).isNotNull();
            assertThat(response.getData().getDiagnostics()).containsExactly(i204Dto);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("clasificación previa sin categoría: responde sin diagnósticos derivados")
        void sinCategoria() {
            ImageClassification previa = ImageClassification.builder().id(3).build();
            when(imageClassificationRepository.findById(3)).thenReturn(Optional.of(previa));
            when(mapper.toResponseDTO(previa)).thenReturn(new ImageClassificationResponseDTO());

            ApiResponse<ImageClassificationResponseDTO> response = service.findById(3);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData().getDiagnostics()).isNull();
            verify(categoryDiagnosticRepository, never()).findDefinitionAt(any(), any());
        }
    }
}
