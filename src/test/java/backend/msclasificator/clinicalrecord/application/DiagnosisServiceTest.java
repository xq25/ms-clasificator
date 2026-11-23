package backend.msclasificator.clinicalrecord.application;

import backend.msclasificator.clinicalrecord.api.dto.DiagnosisCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.DiagnosisResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.DiagnosisSummaryDTO;
import backend.msclasificator.clinicalrecord.api.mapper.DiagnosisMapper;
import backend.msclasificator.clinicalrecord.application.port.out.ClinicalRecordRepository;
import backend.msclasificator.clinicalrecord.application.port.out.DiagnosisRepository;
import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
import backend.msclasificator.clinicalrecord.domain.Diagnosis;
import backend.msclasificator.diagnosiscatalog.application.port.out.MedicalDiagnosticRepository;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiagnosisServiceTest {

    private static final PageQuery QUERY = new PageQuery(0, 10, "id", "asc");

    @Mock private DiagnosisRepository diagnosisRepository;
    @Mock private ClinicalRecordRepository clinicalRecordRepository;
    @Mock private MedicalDiagnosticRepository medicalDiagnosticRepository;
    @Mock private DiagnosisMapper mapper;

    @InjectMocks private DiagnosisService service;

    private final Diagnosis diagnosis = Diagnosis.builder().id(3).build();
    private final DiagnosisSummaryDTO summary = DiagnosisSummaryDTO.builder().id(3).build();
    private final DiagnosisResponseDTO responseDTO = DiagnosisResponseDTO.builder().id(3).build();

    @Nested
    @DisplayName("findAll / count")
    class Listados {

        @Test
        @DisplayName("findAll: construye el PagedResponse con el resumen")
        void findAll() {
            when(diagnosisRepository.findAll(QUERY)).thenReturn(new PageResult<>(List.of(diagnosis), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(diagnosis)).thenReturn(summary);

            ApiResponse<PagedResponse<DiagnosisSummaryDTO>> r = service.findAll(new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Diagnosticos obtenidos exitosamente");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count() {
            when(diagnosisRepository.count()).thenReturn(4L);

            assertThat(service.count().getData()).isEqualTo(4L);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("existe: devuelve el DTO de respuesta")
        void existe() {
            when(diagnosisRepository.findById(3)).thenReturn(Optional.of(diagnosis));
            when(mapper.toResponseDTO(diagnosis)).thenReturn(responseDTO);

            ApiResponse<DiagnosisResponseDTO> r = service.findById(3);

            assertThat(r.getData()).isSameAs(responseDTO);
            assertThat(r.getMessage()).isEqualTo("Diagnosis encontrado exitosamente");
        }

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(diagnosisRepository.findById(3)).thenReturn(Optional.empty());

            assertThat(service.findById(3).getMessage()).isEqualTo("Diagnosis no encontrado con ID: 3");
        }
    }

    @Nested
    @DisplayName("findByClinicalRecordId")
    class FindByClinicalRecordId {

        @Test
        @DisplayName("registro inexistente: error sin consultar diagnósticos")
        void registroInexistente() {
            when(clinicalRecordRepository.existsById(10)).thenReturn(false);

            ApiResponse<PagedResponse<DiagnosisSummaryDTO>> r = service.findByClinicalRecordId(10, new PageRequestDTO());

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Clinical Record no encontrado con ID: 10");
            verify(diagnosisRepository, never()).findByClinicalRecordId(any(), any());
        }

        @Test
        @DisplayName("con diagnósticos: mensaje de encontrados")
        void conDiagnosticos() {
            when(clinicalRecordRepository.existsById(10)).thenReturn(true);
            when(diagnosisRepository.findByClinicalRecordId(10, QUERY)).thenReturn(new PageResult<>(List.of(diagnosis), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(diagnosis)).thenReturn(summary);

            ApiResponse<PagedResponse<DiagnosisSummaryDTO>> r = service.findByClinicalRecordId(10, new PageRequestDTO());

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Diagnosis encontrado por Clinical Record");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("sin diagnósticos: éxito con mensaje de vacío")
        void sinDiagnosticos() {
            when(clinicalRecordRepository.existsById(10)).thenReturn(true);
            when(diagnosisRepository.findByClinicalRecordId(10, QUERY)).thenReturn(new PageResult<>(List.of(), 0, 10, 0, 0, true));

            ApiResponse<PagedResponse<DiagnosisSummaryDTO>> r = service.findByClinicalRecordId(10, new PageRequestDTO());

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("No se encontraron Diagnosis para el Clinical Record ID: 10");
            assertThat(r.getData().getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private final DiagnosisCreateDTO dto = DiagnosisCreateDTO.builder().clinicalRecordId(10).medicalDiagnosticId(20).build();

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("registro inexistente: error sin guardar")
        void registroInexistente() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.empty());

            ApiResponse<DiagnosisResponseDTO> r = service.create(dto);

            assertThat(r.getMessage()).isEqualTo("Clinical Record no encontrado con ID: 10");
            verify(diagnosisRepository, never()).save(any());
        }

        @Test
        @DisplayName("diagnóstico CIE-10 inexistente: error sin guardar")
        void diagnosticoInexistente() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(new ClinicalRecord()));
            when(medicalDiagnosticRepository.findById(20)).thenReturn(Optional.empty());

            ApiResponse<DiagnosisResponseDTO> r = service.create(dto);

            assertThat(r.getMessage()).isEqualTo("Medical Diagnostic no encontrado con ID: 20");
            verify(diagnosisRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: asigna registro y diagnóstico CIE-10, y guarda")
        void valido() {
            ClinicalRecord record = ClinicalRecord.builder().id(10).build();
            MedicalDiagnostic medicalDiagnostic = MedicalDiagnostic.builder().id(20).build();
            Diagnosis nuevo = new Diagnosis();
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(record));
            when(medicalDiagnosticRepository.findById(20)).thenReturn(Optional.of(medicalDiagnostic));
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(diagnosisRepository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(responseDTO);

            ApiResponse<DiagnosisResponseDTO> r = service.create(dto);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Diagnosis creado exitosamente");
            assertThat(nuevo.getClinicalRecord()).isSameAs(record);
            assertThat(nuevo.getMedicalDiagnostic()).isSameAs(medicalDiagnostic);
        }

        @Test
        @DisplayName("error al guardar: la excepción no se captura y se propaga")
        void errorAlGuardarSePropaga() {
            Diagnosis nuevo = new Diagnosis();
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(new ClinicalRecord()));
            when(medicalDiagnosticRepository.findById(20)).thenReturn(Optional.of(new MedicalDiagnostic()));
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(diagnosisRepository.save(nuevo)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> service.create(dto)).isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(diagnosisRepository.findById(3)).thenReturn(Optional.empty());

            assertThat(service.delete(3).getMessage()).isEqualTo("Diagnosis no encontrado con ID: 3");
        }

        @Test
        @DisplayName("válido: elimina")
        void valido() {
            when(diagnosisRepository.findById(3)).thenReturn(Optional.of(diagnosis));

            ApiResponse<Void> r = service.delete(3);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Diagnosis eliminado exitosamente");
            verify(diagnosisRepository).delete(diagnosis);
        }

        @Test
        @DisplayName("violación de integridad: error con prefijo")
        void violacionIntegridad() {
            when(diagnosisRepository.findById(3)).thenReturn(Optional.of(diagnosis));
            doThrow(new DataIntegrityViolationException("fk")).when(diagnosisRepository).delete(diagnosis);

            assertThat(service.delete(3).getMessage()).isEqualTo("Violacion a integridad de la base de datos: fk");
        }
    }
}
