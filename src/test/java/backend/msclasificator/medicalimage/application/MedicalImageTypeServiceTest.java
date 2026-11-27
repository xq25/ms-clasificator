package backend.msclasificator.medicalimage.application;

import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.doctor.application.port.out.EvaluationAreaRepository;
import backend.msclasificator.doctor.domain.EvaluationArea;
import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeCreateDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeResponseDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeSummaryDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeUpdateDTO;
import backend.msclasificator.medicalimage.api.mapper.MedicalImageTypeMapper;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageRepository;
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
class MedicalImageTypeServiceTest {

    private static final PageQuery QUERY = new PageQuery(0, 10, "id", "asc");

    @Mock private MedicalImageTypeRepository medicalImageTypeRepository;
    @Mock private MedicalImageRepository medicalImageRepository;
    @Mock private EvaluationAreaRepository evaluationAreaRepository;
    @Mock private MedicalImageTypeMapper mapper;

    @InjectMocks private MedicalImageTypeService service;

    private EvaluationArea cardio;
    private MedicalImageType ecg;
    private final MedicalImageTypeResponseDTO responseDTO = MedicalImageTypeResponseDTO.builder().id(1).build();
    private final MedicalImageTypeSummaryDTO summary = MedicalImageTypeSummaryDTO.builder().id(1).build();

    @BeforeEach
    void setUp() {
        cardio = EvaluationArea.builder().id(1).codeArea("CARDIO").name("Cardiología").build();
        ecg = MedicalImageType.builder().id(1).name("Electrocardiograma").evaluationArea(cardio).build();
    }

    private static MedicalImageTypeUpdateDTO updateDTO(String name) {
        MedicalImageTypeUpdateDTO dto = new MedicalImageTypeUpdateDTO();
        dto.setName(name);
        return dto;
    }

    @Nested
    @DisplayName("findAll / searchByName / count / findById")
    class Lecturas {

        @Test
        @DisplayName("findAll: página de resúmenes")
        void findAll() {
            when(medicalImageTypeRepository.findAll(QUERY)).thenReturn(new PageResult<>(List.of(ecg), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(ecg)).thenReturn(summary);

            ApiResponse<PagedResponse<MedicalImageTypeSummaryDTO>> r = service.findAll(new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Tipos de imagen obtenidos exitosamente");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("searchByName: página de resúmenes")
        void searchByName() {
            when(medicalImageTypeRepository.searchByName("electro", QUERY)).thenReturn(new PageResult<>(List.of(ecg), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(ecg)).thenReturn(summary);

            ApiResponse<PagedResponse<MedicalImageTypeSummaryDTO>> r = service.searchByName("electro", new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Resultados de búsqueda por nombre");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("searchByName con error inesperado: ApiResponse.error con prefijo")
        void searchByNameError() {
            when(medicalImageTypeRepository.searchByName("x", QUERY)).thenThrow(new IllegalStateException("BD caída"));

            assertThat(service.searchByName("x", new PageRequestDTO()).getMessage())
                    .isEqualTo("Error al buscar tipos de imagen por nombre: BD caída");
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count() {
            when(medicalImageTypeRepository.count()).thenReturn(13L);

            assertThat(service.count().getData()).isEqualTo(13L);
        }

        @Test
        @DisplayName("findById existente: devuelve el DTO")
        void findByIdExiste() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(mapper.toResponseDTO(ecg)).thenReturn(responseDTO);

            ApiResponse<MedicalImageTypeResponseDTO> r = service.findById(1);

            assertThat(r.getData()).isSameAs(responseDTO);
            assertThat(r.getMessage()).isEqualTo("Tipo de imagen obtenido exitosamente");
        }

        @Test
        @DisplayName("findById inexistente: error con el ID")
        void findByIdNoExiste() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.findById(1).getMessage()).isEqualTo("Tipo de imagen médica no encontrado con ID: 1");
        }
    }

    @Nested
    @DisplayName("findByEvaluationAreaId")
    class PorArea {

        @Test
        @DisplayName("área inexistente: error")
        void areaInexistente() {
            when(evaluationAreaRepository.existsById(1)).thenReturn(false);

            ApiResponse<List<MedicalImageTypeSummaryDTO>> r = service.findByEvaluationAreaId(1);

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Área de evaluación no encontrada con ID: 1");
        }

        @Test
        @DisplayName("con tipos: lista de resúmenes")
        void conTipos() {
            when(evaluationAreaRepository.existsById(1)).thenReturn(true);
            when(medicalImageTypeRepository.findByEvaluationAreaId(1)).thenReturn(List.of(ecg));
            when(mapper.toSummaryDTO(ecg)).thenReturn(summary);

            ApiResponse<List<MedicalImageTypeSummaryDTO>> r = service.findByEvaluationAreaId(1);

            assertThat(r.getMessage()).isEqualTo("Tipos de imagen asociados al área de evaluación con ID: 1 obtenidos exitosamente");
            assertThat(r.getData()).containsExactly(summary);
        }

        @Test
        @DisplayName("sin tipos: éxito con lista vacía y mensaje de vacío")
        void sinTipos() {
            when(evaluationAreaRepository.existsById(1)).thenReturn(true);
            when(medicalImageTypeRepository.findByEvaluationAreaId(1)).thenReturn(List.of());

            ApiResponse<List<MedicalImageTypeSummaryDTO>> r = service.findByEvaluationAreaId(1);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("No se encontraron tipos de imagen asociados al área de evaluación con ID: 1");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private final MedicalImageTypeCreateDTO dto = MedicalImageTypeCreateDTO.builder().name("Holter").build();

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("nombre duplicado: error sin guardar")
        void nombreDuplicado() {
            when(medicalImageTypeRepository.existsByName("Holter")).thenReturn(true);

            assertThat(service.create(dto).getMessage()).isEqualTo("Ya existe un tipo de imagen con el nombre: Holter");
            verify(medicalImageTypeRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: guarda la entidad del mapper")
        void valido() {
            MedicalImageType nuevo = new MedicalImageType();
            when(medicalImageTypeRepository.existsByName("Holter")).thenReturn(false);
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(medicalImageTypeRepository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(responseDTO);

            ApiResponse<MedicalImageTypeResponseDTO> r = service.create(dto);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Tipo de imagen creado exitosamente");
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
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.update(1, updateDTO("ECG")).getMessage()).isEqualTo("Tipo de imagen médica no encontrado con ID: 1");
        }

        @Test
        @DisplayName("nombre de otro tipo: error sin guardar")
        void nombreDeOtro() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(medicalImageTypeRepository.existsByName("Rx de tórax")).thenReturn(true);

            assertThat(service.update(1, updateDTO("Rx de tórax")).getMessage()).isEqualTo("Ya existe un tipo de imagen con el nombre: Rx de tórax");
            verify(medicalImageTypeRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: cambia el nombre y conserva el área")
        void valido() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(medicalImageTypeRepository.existsByName("ECG de 12 derivaciones")).thenReturn(false);
            when(medicalImageTypeRepository.save(ecg)).thenReturn(ecg);
            when(mapper.toResponseDTO(ecg)).thenReturn(responseDTO);

            ApiResponse<MedicalImageTypeResponseDTO> r = service.update(1, updateDTO("ECG de 12 derivaciones"));

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Tipo de imagen actualizado exitosamente");
            assertThat(ecg.getName()).isEqualTo("ECG de 12 derivaciones");
            assertThat(ecg.getEvaluationArea()).isSameAs(cardio);
        }

        @Test
        @DisplayName("BUG CONOCIDO #22: su propio nombre se rechaza como duplicado (no se excluye el propio tipo)")
        void propioNombreRechazado() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(medicalImageTypeRepository.existsByName("Electrocardiograma")).thenReturn(true);

            ApiResponse<MedicalImageTypeResponseDTO> r = service.update(1, updateDTO("Electrocardiograma"));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Ya existe un tipo de imagen con el nombre: Electrocardiograma");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.delete(1).getMessage()).isEqualTo("Tipo de imagen médica no encontrado con ID: 1");
        }

        @Test
        @DisplayName("con imágenes asociadas: error sin eliminar")
        void conImagenes() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(medicalImageRepository.existsByMedicalImageTypeId(1)).thenReturn(true);

            ApiResponse<Void> r = service.delete(1);

            assertThat(r.getMessage()).isEqualTo("No se puede eliminar el tipo de imagen porque tiene imágenes médicas asociadas");
            verify(medicalImageTypeRepository, never()).delete(any());
        }

        @Test
        @DisplayName("sin imágenes: elimina")
        void valido() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(medicalImageRepository.existsByMedicalImageTypeId(1)).thenReturn(false);

            ApiResponse<Void> r = service.delete(1);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Tipo de imagen eliminado exitosamente");
            verify(medicalImageTypeRepository).delete(ecg);
        }

        @Test
        @DisplayName("BUG CONOCIDO #23: con datasets (incluso retirados) elimina igual; en BD la cascada los borra físicamente")
        void conDatasets() {
            ecg.setDatasets(List.of(Dataset.builder().id(4).name("ECG").build()));
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(medicalImageRepository.existsByMedicalImageTypeId(1)).thenReturn(false);

            ApiResponse<Void> r = service.delete(1);

            assertThat(r.isSuccess()).isTrue();
            verify(medicalImageTypeRepository).delete(any());
        }

        @Test
        @DisplayName("violación de integridad: error con prefijo")
        void violacionIntegridad() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(medicalImageRepository.existsByMedicalImageTypeId(1)).thenReturn(false);
            doThrow(new DataIntegrityViolationException("fk")).when(medicalImageTypeRepository).delete(ecg);

            assertThat(service.delete(1).getMessage()).isEqualTo("Violacion de integridad de la base de datos: fk");
        }
    }

    @Nested
    @DisplayName("assignEvaluationArea / removeEvaluationArea")
    class Areas {

        @Test
        @DisplayName("asignar con tipo inexistente: error")
        void asignarTipoInexistente() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.assignEvaluationArea(1, 2).getMessage()).isEqualTo("Tipo de imagen médica no encontrado con ID: 1");
        }

        @Test
        @DisplayName("asignar con área inexistente: error sin guardar")
        void asignarAreaInexistente() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(evaluationAreaRepository.findById(2)).thenReturn(Optional.empty());

            assertThat(service.assignEvaluationArea(1, 2).getMessage()).isEqualTo("Área de evaluación no encontrada con ID: 2");
            verify(medicalImageTypeRepository, never()).save(any());
        }

        @Test
        @DisplayName("asignar válido: reemplaza el área")
        void asignarValido() {
            EvaluationArea cardInt = EvaluationArea.builder().id(2).codeArea("CARDINT").build();
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(evaluationAreaRepository.findById(2)).thenReturn(Optional.of(cardInt));
            when(medicalImageTypeRepository.save(ecg)).thenReturn(ecg);
            when(mapper.toResponseDTO(ecg)).thenReturn(responseDTO);

            ApiResponse<MedicalImageTypeResponseDTO> r = service.assignEvaluationArea(1, 2);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Área de evaluación asignada exitosamente");
            assertThat(ecg.getEvaluationArea()).isSameAs(cardInt);
        }

        @Test
        @DisplayName("remover con tipo inexistente: error")
        void removerTipoInexistente() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.empty());

            assertThat(service.removeEvaluationArea(1).getMessage()).isEqualTo("Tipo de imagen médica no encontrado con ID: 1");
        }

        @Test
        @DisplayName("remover sin área asignada: error sin guardar")
        void removerSinArea() {
            ecg.setEvaluationArea(null);
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));

            assertThat(service.removeEvaluationArea(1).getMessage())
                    .isEqualTo("El tipo de imagen médica no tiene un área de evaluación asignada para ser eliminada");
            verify(medicalImageTypeRepository, never()).save(any());
        }

        @Test
        @DisplayName("remover válido: queda sin área")
        void removerValido() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(ecg));
            when(medicalImageTypeRepository.save(ecg)).thenReturn(ecg);
            when(mapper.toResponseDTO(ecg)).thenReturn(responseDTO);

            ApiResponse<MedicalImageTypeResponseDTO> r = service.removeEvaluationArea(1);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Área de evaluación removida exitosamente");
            assertThat(ecg.getEvaluationArea()).isNull();
        }
    }
}
