package backend.msclasificator.classification.application;

import backend.msclasificator.classification.api.dto.ImageClassificationCreateDTO;
import backend.msclasificator.classification.api.dto.ImageClassificationResponseDTO;
import backend.msclasificator.classification.api.mapper.ImageClassificationMapper;
import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.classification.domain.ImageClassification;
import backend.msclasificator.dataset.application.port.out.DatasetRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Cubre create (reglas de autorización, dataset y duplicado); el resto de ImageClassificationService queda pendiente (ADR-008)
@ExtendWith(MockitoExtension.class)
class ImageClassificationServiceTest {

    @Mock private ImageClassificationRepository imageClassificationRepository;
    @Mock private ImageClassificationMapper mapper;
    @Mock private DoctorRepository doctorRepository;
    @Mock private MedicalImageRepository medicalImageRepository;
    @Mock private DoctorAreaRepository doctorAreaRepository;
    @Mock private DatasetRepository datasetRepository;

    @InjectMocks private ImageClassificationService service;

    private Doctor doctor;
    private MedicalImage imagen;
    private final ImageClassificationCreateDTO dto = ImageClassificationCreateDTO.builder().doctorId(2).medicalImgId(50).build();

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder().id(2).code("MED-01").build();
        EvaluationArea cardio = EvaluationArea.builder().id(1).codeArea("CARDIO").build();
        MedicalImageType ecg = MedicalImageType.builder().id(1).name("Electrocardiograma").evaluationArea(cardio).build();
        imagen = MedicalImage.builder().id(50).medicalImageType(ecg).build();
    }

    private void stubDoctorEImagen() {
        when(doctorRepository.findById(2)).thenReturn(Optional.of(doctor));
        when(medicalImageRepository.findById(50)).thenReturn(Optional.of(imagen));
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
        @DisplayName("tipo de imagen sin dataset: error y no guarda")
        void sinDataset() {
            stubDoctorEImagen();
            when(datasetRepository.existsByMedicalImageTypeId(1)).thenReturn(false);

            ApiResponse<ImageClassificationResponseDTO> response = service.create(dto);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("El tipo de imagen médica no tiene un dataset configurado; no se puede clasificar");
            verify(imageClassificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("tipo de imagen sin área de evaluación: error")
        void sinArea() {
            imagen.getMedicalImageType().setEvaluationArea(null);
            stubDoctorEImagen();
            when(datasetRepository.existsByMedicalImageTypeId(1)).thenReturn(true);

            assertThat(service.create(dto).getMessage())
                    .startsWith("El tipo de imagen médica no tiene un área de evaluación asignada");
            verify(imageClassificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("doctor fuera del área del tipo de imagen: error")
        void doctorSinAutorizacion() {
            stubDoctorEImagen();
            when(datasetRepository.existsByMedicalImageTypeId(1)).thenReturn(true);
            when(doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(false);

            assertThat(service.create(dto).getMessage()).isEqualTo("El doctor no tiene autorización para diagnosticar ese tipo de imagen médica");
            verify(imageClassificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("el doctor ya clasificó esa imagen: error")
        void duplicado() {
            stubDoctorEImagen();
            when(datasetRepository.existsByMedicalImageTypeId(1)).thenReturn(true);
            when(doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(true);
            when(imageClassificationRepository.findByDoctorIdAndMedicalImageId(2, 50))
                    .thenReturn(Optional.of(new ImageClassification()));

            assertThat(service.create(dto).getMessage()).isEqualTo("Ya existe un diagnóstico dado por ese doctor a esa imagen médica");
            verify(imageClassificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: asocia doctor, imagen y fecha, y guarda")
        void valido() {
            ImageClassification nueva = new ImageClassification();
            stubDoctorEImagen();
            when(datasetRepository.existsByMedicalImageTypeId(1)).thenReturn(true);
            when(doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(true);
            when(imageClassificationRepository.findByDoctorIdAndMedicalImageId(2, 50)).thenReturn(Optional.empty());
            when(mapper.toEntity(dto)).thenReturn(nueva);
            when(imageClassificationRepository.save(nueva)).thenReturn(nueva);
            when(mapper.toResponseDTO(nueva)).thenReturn(new ImageClassificationResponseDTO());

            ApiResponse<ImageClassificationResponseDTO> response = service.create(dto);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Diagnóstico de imagen creado exitosamente");
            assertThat(nueva.getDoctor()).isSameAs(doctor);
            assertThat(nueva.getMedicalImg()).isSameAs(imagen);
            assertThat(nueva.getDiagnosticDate()).isNotNull();
        }
    }
}
