package backend.msclasificator.clinicalrecord.application;

import backend.msclasificator.clinicalrecord.api.dto.PatientDatumCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.PatientDatumResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.PatientDatumSummaryDTO;
import backend.msclasificator.clinicalrecord.api.dto.PatientDatumUpdateDTO;
import backend.msclasificator.clinicalrecord.api.mapper.PatientDatumMapper;
import backend.msclasificator.clinicalrecord.application.port.out.ClinicalRecordRepository;
import backend.msclasificator.clinicalrecord.application.port.out.PatientDatumRepository;
import backend.msclasificator.clinicalrecord.application.port.out.PrimitiveDatumRepository;
import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
import backend.msclasificator.clinicalrecord.domain.PatientDatum;
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
class PatientDatumServiceTest {

    private static final PageQuery QUERY = new PageQuery(0, 10, "id", "asc");

    @Mock private PatientDatumRepository patientDatumRepository;
    @Mock private ClinicalRecordRepository clinicalRecordRepository;
    @Mock private PrimitiveDatumRepository primitiveDatumRepository;
    @Mock private PatientDatumMapper mapper;

    @InjectMocks private PatientDatumService service;

    private final PrimitiveDatum frecuenciaCardiaca = PrimitiveDatum.builder()
            .id(5).name("Frecuencia cardíaca").type(PrimitiveType.INTEGER).unit(PrimitiveUnit.NONE).build();
    private final PatientDatum datum = PatientDatum.builder().id(8).description("72").primitiveDatum(frecuenciaCardiaca).build();
    private final PatientDatumSummaryDTO summary = PatientDatumSummaryDTO.builder().id(8).build();
    private final PatientDatumResponseDTO responseDTO = PatientDatumResponseDTO.builder().id(8).build();

    private static PatientDatumUpdateDTO updateDTO(String description) {
        PatientDatumUpdateDTO dto = new PatientDatumUpdateDTO();
        dto.setDescription(description);
        return dto;
    }

    @Nested
    @DisplayName("findAll / count / findById")
    class Lecturas {

        @Test
        @DisplayName("findAll: construye el PagedResponse con el resumen")
        void findAll() {
            when(patientDatumRepository.findAll(QUERY)).thenReturn(new PageResult<>(List.of(datum), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(datum)).thenReturn(summary);

            ApiResponse<PagedResponse<PatientDatumSummaryDTO>> r = service.findAll(new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Patient Datum obtenidos exitosamente");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count() {
            when(patientDatumRepository.count()).thenReturn(9L);

            assertThat(service.count().getData()).isEqualTo(9L);
        }

        @Test
        @DisplayName("findById existente: devuelve el DTO")
        void findByIdExiste() {
            when(patientDatumRepository.findById(8)).thenReturn(Optional.of(datum));
            when(mapper.toResponseDTO(datum)).thenReturn(responseDTO);

            ApiResponse<PatientDatumResponseDTO> r = service.findById(8);

            assertThat(r.getData()).isSameAs(responseDTO);
            assertThat(r.getMessage()).isEqualTo("Patient Datum encontrado exitosamente");
        }

        @Test
        @DisplayName("findById inexistente: error con el ID")
        void findByIdNoExiste() {
            when(patientDatumRepository.findById(8)).thenReturn(Optional.empty());

            assertThat(service.findById(8).getMessage()).isEqualTo("Patient Datum no encontrado con ID: 8");
        }
    }

    @Nested
    @DisplayName("findByClinicalRecordId")
    class FindByClinicalRecordId {

        @Test
        @DisplayName("registro inexistente: error (mensaje con errata 'encontre')")
        void registroInexistente() {
            when(clinicalRecordRepository.existsById(10)).thenReturn(false);

            ApiResponse<List<PatientDatumResponseDTO>> r = service.findByClinicalRecordId(10);

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("No se encontre un clinicalRecord con id : 10");
            verify(patientDatumRepository, never()).findByClinicalRecordId(any());
        }

        @Test
        @DisplayName("con datos: lista de respuestas")
        void conDatos() {
            when(clinicalRecordRepository.existsById(10)).thenReturn(true);
            when(patientDatumRepository.findByClinicalRecordId(10)).thenReturn(List.of(datum));
            when(mapper.toResponseDTO(datum)).thenReturn(responseDTO);

            ApiResponse<List<PatientDatumResponseDTO>> r = service.findByClinicalRecordId(10);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Patient Datum encontrados exitosamente para el Clinical Record");
            assertThat(r.getData()).containsExactly(responseDTO);
        }

        @Test
        @DisplayName("sin datos: éxito con lista vacía y mensaje de vacío")
        void sinDatos() {
            when(clinicalRecordRepository.existsById(10)).thenReturn(true);
            when(patientDatumRepository.findByClinicalRecordId(10)).thenReturn(List.of());

            ApiResponse<List<PatientDatumResponseDTO>> r = service.findByClinicalRecordId(10);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("No se encontraron datos del paciente para esta visita medica: 10");
            assertThat(r.getData()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByPrimitiveDatumId")
    class FindByPrimitiveDatumId {

        @Test
        @DisplayName("dato primitivo inexistente: error (mensaje con errata 'encontre')")
        void inexistente() {
            when(primitiveDatumRepository.existsById(5)).thenReturn(false);

            ApiResponse<List<PatientDatumSummaryDTO>> r = service.findByPrimitiveDatumId(5);

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("No se encontre un primitiveDatum con id : 5");
        }

        @Test
        @DisplayName("con datos: lista de resúmenes")
        void conDatos() {
            when(primitiveDatumRepository.existsById(5)).thenReturn(true);
            when(patientDatumRepository.findByPrimitiveDatumId(5)).thenReturn(List.of(datum));
            when(mapper.toSummaryDTO(datum)).thenReturn(summary);

            ApiResponse<List<PatientDatumSummaryDTO>> r = service.findByPrimitiveDatumId(5);

            assertThat(r.getMessage()).isEqualTo("Patient Datum encontrados exitosamente para el Primitive Datum");
            assertThat(r.getData()).containsExactly(summary);
        }

        @Test
        @DisplayName("sin datos: éxito con lista vacía y mensaje de vacío")
        void sinDatos() {
            when(primitiveDatumRepository.existsById(5)).thenReturn(true);
            when(patientDatumRepository.findByPrimitiveDatumId(5)).thenReturn(List.of());

            ApiResponse<List<PatientDatumSummaryDTO>> r = service.findByPrimitiveDatumId(5);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("No se encontraron Patient Datum para el Primitive Datum con ID: 5");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private PatientDatumCreateDTO dto(String description) {
            return PatientDatumCreateDTO.builder().clinicalRecordId(10).primitiveDatumId(5).description(description).build();
        }

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("registro inexistente: error sin guardar")
        void registroInexistente() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.empty());

            assertThat(service.create(dto("72")).getMessage()).isEqualTo("Clinical Record no encontrado con ID: 10");
            verify(patientDatumRepository, never()).save(any());
        }

        @Test
        @DisplayName("dato primitivo inexistente: error sin guardar")
        void primitivoInexistente() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(new ClinicalRecord()));
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.empty());

            assertThat(service.create(dto("72")).getMessage()).isEqualTo("Primitive Datum no encontrado con ID: 5");
            verify(patientDatumRepository, never()).save(any());
        }

        @Test
        @DisplayName("valor incompatible con el tipo del dato primitivo: error sin guardar")
        void valorIncompatible() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(new ClinicalRecord()));
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.of(frecuenciaCardiaca));

            ApiResponse<PatientDatumResponseDTO> r = service.create(dto("setenta"));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("El valor 'setenta' no es compatible con el tipo INTEGER");
            verify(patientDatumRepository, never()).save(any());
        }

        @Test
        @DisplayName("valor en blanco: se acepta sin validar el tipo")
        void valorEnBlanco() {
            PatientDatumCreateDTO dto = dto("");
            PatientDatum nuevo = new PatientDatum();
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(new ClinicalRecord()));
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.of(frecuenciaCardiaca));
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(patientDatumRepository.save(nuevo)).thenReturn(nuevo);

            assertThat(service.create(dto).isSuccess()).isTrue();
        }

        @Test
        @DisplayName("válido: asigna registro y dato primitivo, y guarda")
        void valido() {
            PatientDatumCreateDTO dto = dto("72");
            ClinicalRecord record = ClinicalRecord.builder().id(10).build();
            PatientDatum nuevo = new PatientDatum();
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(record));
            when(primitiveDatumRepository.findById(5)).thenReturn(Optional.of(frecuenciaCardiaca));
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(patientDatumRepository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(responseDTO);

            ApiResponse<PatientDatumResponseDTO> r = service.create(dto);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Patient Datum creado exitosamente");
            assertThat(nuevo.getClinicalRecord()).isSameAs(record);
            assertThat(nuevo.getPrimitiveDatum()).isSameAs(frecuenciaCardiaca);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.update(8, null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(patientDatumRepository.findById(8)).thenReturn(Optional.empty());

            assertThat(service.update(8, updateDTO("80")).getMessage()).isEqualTo("Patient Datum no encontrado con ID: 8");
        }

        @Test
        @DisplayName("válido: solo cambia la descripción")
        void valido() {
            when(patientDatumRepository.findById(8)).thenReturn(Optional.of(datum));
            when(patientDatumRepository.save(datum)).thenReturn(datum);
            when(mapper.toResponseDTO(datum)).thenReturn(responseDTO);

            ApiResponse<PatientDatumResponseDTO> r = service.update(8, updateDTO("80"));

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Patient Datum actualizado exitosamente");
            assertThat(datum.getDescription()).isEqualTo("80");
            assertThat(datum.getPrimitiveDatum()).isSameAs(frecuenciaCardiaca);
        }

        @Test
        @DisplayName("valor incompatible con el tipo del dato primitivo: error sin guardar (bug #13)")
        void valorIncompatible() {
            when(patientDatumRepository.findById(8)).thenReturn(Optional.of(datum));

            ApiResponse<PatientDatumResponseDTO> r = service.update(8, updateDTO("setenta"));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("El valor 'setenta' no es compatible con el tipo INTEGER");
            assertThat(datum.getDescription()).isEqualTo("72");
            verify(patientDatumRepository, never()).save(any());
        }

        @Test
        @DisplayName("valor en blanco: se acepta sin validar el tipo (igual que create)")
        void valorEnBlanco() {
            when(patientDatumRepository.findById(8)).thenReturn(Optional.of(datum));
            when(patientDatumRepository.save(datum)).thenReturn(datum);

            assertThat(service.update(8, updateDTO("")).isSuccess()).isTrue();
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(patientDatumRepository.findById(8)).thenReturn(Optional.empty());

            assertThat(service.delete(8).getMessage()).isEqualTo("Patient Datum no encontrado con ID: 8");
        }

        @Test
        @DisplayName("válido: elimina")
        void valido() {
            when(patientDatumRepository.findById(8)).thenReturn(Optional.of(datum));

            ApiResponse<Void> r = service.delete(8);

            assertThat(r.getMessage()).isEqualTo("Patient Datum eliminado exitosamente");
            verify(patientDatumRepository).delete(datum);
        }

        @Test
        @DisplayName("violación de integridad: error con prefijo")
        void violacionIntegridad() {
            when(patientDatumRepository.findById(8)).thenReturn(Optional.of(datum));
            doThrow(new DataIntegrityViolationException("fk")).when(patientDatumRepository).delete(datum);

            assertThat(service.delete(8).getMessage()).isEqualTo("Violacion a integridad de la base de datos: fk");
        }
    }
}
