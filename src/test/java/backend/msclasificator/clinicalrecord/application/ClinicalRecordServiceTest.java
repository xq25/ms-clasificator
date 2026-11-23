package backend.msclasificator.clinicalrecord.application;

import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordSummaryDTO;
import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordUpdateDTO;
import backend.msclasificator.clinicalrecord.api.mapper.ClinicalRecordMapper;
import backend.msclasificator.clinicalrecord.application.port.out.ClinicalRecordRepository;
import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageRepository;
import backend.msclasificator.medicalimage.domain.MedicalImage;
import backend.msclasificator.patient.application.port.out.PatientRepository;
import backend.msclasificator.patient.domain.Patient;
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
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicalRecordServiceTest {

    private static final PageQuery QUERY = new PageQuery(0, 10, "id", "asc");
    private static final long UN_DIA = 24L * 60 * 60 * 1000;

    @Mock private ClinicalRecordRepository clinicalRecordRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private MedicalImageRepository medicalImageRepository;
    @Mock private ClinicalRecordMapper mapper;

    @InjectMocks private ClinicalRecordService service;

    private Patient patient;
    private ClinicalRecord record;
    private ClinicalRecordResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        patient = Patient.builder().id(1).document("CC1").build();
        record = ClinicalRecord.builder().id(10).chiefComplaint("Dolor torácico").visitDate(new Date(0)).patient(patient).build();
        responseDTO = ClinicalRecordResponseDTO.builder().id(10).build();
    }

    private static ClinicalRecordUpdateDTO updateDTO(String chiefComplaint, Date visitDate) {
        ClinicalRecordUpdateDTO dto = new ClinicalRecordUpdateDTO();
        dto.setChiefComplaint(chiefComplaint);
        dto.setVisitDate(visitDate);
        return dto;
    }

    @Nested
    @DisplayName("findAll / count")
    class Listados {

        @Test
        @DisplayName("findAll: construye el PagedResponse con el resumen")
        void findAll() {
            ClinicalRecordSummaryDTO summary = ClinicalRecordSummaryDTO.builder().id(10).build();
            when(clinicalRecordRepository.findAll(QUERY)).thenReturn(new PageResult<>(List.of(record), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(record)).thenReturn(summary);

            ApiResponse<PagedResponse<ClinicalRecordSummaryDTO>> r = service.findAll(new PageRequestDTO());

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Registros médicos obtenidos exitosamente");
            assertThat(r.getData().getContent()).containsExactly(summary);
            assertThat(r.getData().getTotalElements()).isEqualTo(1);
            assertThat(r.getData().isLast()).isTrue();
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count() {
            when(clinicalRecordRepository.count()).thenReturn(7L);

            ApiResponse<Long> r = service.count();

            assertThat(r.getData()).isEqualTo(7L);
            assertThat(r.getMessage()).isEqualTo("Total de registros médicos");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("existe: devuelve el DTO de respuesta")
        void existe() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(record));
            when(mapper.toResponseDTO(record)).thenReturn(responseDTO);

            ApiResponse<ClinicalRecordResponseDTO> r = service.findById(10);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getData()).isSameAs(responseDTO);
            assertThat(r.getMessage()).isEqualTo("Registro médico obtenido exitosamente");
        }

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.empty());

            ApiResponse<ClinicalRecordResponseDTO> r = service.findById(10);

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Clinical Record no encontrado con ID: 10");
        }

        @Test
        @DisplayName("error inesperado: se convierte en ApiResponse.error con prefijo")
        void errorInesperado() {
            when(clinicalRecordRepository.findById(10)).thenThrow(new IllegalStateException("BD caída"));

            ApiResponse<ClinicalRecordResponseDTO> r = service.findById(10);

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Error al obtener registro médico: BD caída");
        }
    }

    @Nested
    @DisplayName("findByPatientId")
    class FindByPatientId {

        @Test
        @DisplayName("paciente inexistente: error sin consultar registros")
        void pacienteInexistente() {
            when(patientRepository.existsById(1)).thenReturn(false);

            ApiResponse<PagedResponse<ClinicalRecordSummaryDTO>> r = service.findByPatientId(1, new PageRequestDTO());

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Paciente no encontrado con ID: 1");
            verify(clinicalRecordRepository, never()).findByPatientId(any(), any());
        }

        @Test
        @DisplayName("paciente existente: página de resúmenes")
        void pacienteExistente() {
            ClinicalRecordSummaryDTO summary = ClinicalRecordSummaryDTO.builder().id(10).build();
            when(patientRepository.existsById(1)).thenReturn(true);
            when(clinicalRecordRepository.findByPatientId(1, QUERY)).thenReturn(new PageResult<>(List.of(record), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(record)).thenReturn(summary);

            ApiResponse<PagedResponse<ClinicalRecordSummaryDTO>> r = service.findByPatientId(1, new PageRequestDTO());

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Registros médicos obtenidos exitosamente");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("error inesperado: se convierte en ApiResponse.error con prefijo")
        void errorInesperado() {
            when(patientRepository.existsById(1)).thenThrow(new IllegalStateException("BD caída"));

            ApiResponse<PagedResponse<ClinicalRecordSummaryDTO>> r = service.findByPatientId(1, new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Error al listar registros médicos: BD caída");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        private ClinicalRecordCreateDTO dto(Date visitDate) {
            return ClinicalRecordCreateDTO.builder().patientId(1).chiefComplaint("Dolor torácico").visitDate(visitDate).build();
        }

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.create(null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("paciente inexistente: error sin guardar")
        void pacienteInexistente() {
            when(patientRepository.findById(1)).thenReturn(Optional.empty());

            ApiResponse<ClinicalRecordResponseDTO> r = service.create(dto(new Date(0)));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("El paciente asignado no existe, con id 1");
            verify(clinicalRecordRepository, never()).save(any());
        }

        @Test
        @DisplayName("fecha de visita futura: error sin guardar")
        void fechaFutura() {
            when(patientRepository.findById(1)).thenReturn(Optional.of(patient));

            ApiResponse<ClinicalRecordResponseDTO> r = service.create(dto(new Date(System.currentTimeMillis() + UN_DIA)));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("La fecha de la visita no puede ser superior a la fecha actual");
            verify(clinicalRecordRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: asigna el paciente y guarda")
        void valido() {
            ClinicalRecordCreateDTO dto = dto(new Date(0));
            ClinicalRecord nuevo = new ClinicalRecord();
            when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(clinicalRecordRepository.save(nuevo)).thenReturn(nuevo);
            when(mapper.toResponseDTO(nuevo)).thenReturn(responseDTO);

            ApiResponse<ClinicalRecordResponseDTO> r = service.create(dto);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Registro médico creado exitosamente");
            assertThat(r.getData()).isSameAs(responseDTO);
            assertThat(nuevo.getPatient()).isSameAs(patient);
        }

        @Test
        @DisplayName("error al guardar: se convierte en ApiResponse.error con prefijo")
        void errorAlGuardar() {
            ClinicalRecordCreateDTO dto = dto(new Date(0));
            ClinicalRecord nuevo = new ClinicalRecord();
            when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
            when(mapper.toEntity(dto)).thenReturn(nuevo);
            when(clinicalRecordRepository.save(nuevo)).thenThrow(new DataIntegrityViolationException("fk"));

            ApiResponse<ClinicalRecordResponseDTO> r = service.create(dto);

            assertThat(r.getMessage()).isEqualTo("Error al crear registro médico: fk");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("DTO nulo: error")
        void dtoNulo() {
            assertThat(service.update(10, null).getMessage()).isEqualTo("El DTO no puede ser nulo");
        }

        @Test
        @DisplayName("no existe: error con el ID")
        void noExiste() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.empty());

            ApiResponse<ClinicalRecordResponseDTO> r = service.update(10, updateDTO("x", new Date(0)));

            assertThat(r.getMessage()).isEqualTo("Clinical Record no encontrado con ID: 10");
        }

        @Test
        @DisplayName("válido: actualiza motivo y fecha, conserva el paciente")
        void valido() {
            Date nuevaFecha = new Date(1000);
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(record));
            when(clinicalRecordRepository.save(record)).thenReturn(record);
            when(mapper.toResponseDTO(record)).thenReturn(responseDTO);

            ApiResponse<ClinicalRecordResponseDTO> r = service.update(10, updateDTO("Control", nuevaFecha));

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Registro médico actualizado exitosamente");
            assertThat(record.getChiefComplaint()).isEqualTo("Control");
            assertThat(record.getVisitDate()).isEqualTo(nuevaFecha);
            assertThat(record.getPatient()).isSameAs(patient);
        }

        @Test
        @DisplayName("BUG CONOCIDO #12: acepta una fecha de visita futura (create la rechaza)")
        void aceptaFechaFutura() {
            Date futura = new Date(System.currentTimeMillis() + UN_DIA);
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(record));
            when(clinicalRecordRepository.save(record)).thenReturn(record);
            when(mapper.toResponseDTO(record)).thenReturn(responseDTO);

            ApiResponse<ClinicalRecordResponseDTO> r = service.update(10, updateDTO("Control", futura));

            assertThat(r.isSuccess()).isTrue();
            assertThat(record.getVisitDate()).isEqualTo(futura);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("no existe: error sin tocar imágenes")
        void noExiste() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.empty());

            ApiResponse<Void> r = service.delete(10);

            assertThat(r.getMessage()).isEqualTo("Clinical Record no encontrado con ID: 10");
            verify(medicalImageRepository, never()).findByClinicalRecordId(any());
        }

        @Test
        @DisplayName("válido: desvincula las imágenes y luego elimina el registro")
        void valido() {
            MedicalImage img1 = MedicalImage.builder().imageKey("a").clinicalRecord(record).build();
            MedicalImage img2 = MedicalImage.builder().imageKey("b").clinicalRecord(record).build();
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(record));
            when(medicalImageRepository.findByClinicalRecordId(10)).thenReturn(List.of(img1, img2));

            ApiResponse<Void> r = service.delete(10);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Registro médico eliminado correctamente");
            assertThat(img1.getClinicalRecord()).isNull();
            assertThat(img2.getClinicalRecord()).isNull();
            InOrder orden = inOrder(medicalImageRepository, clinicalRecordRepository);
            orden.verify(medicalImageRepository).findByClinicalRecordId(10);
            orden.verify(clinicalRecordRepository).delete(record);
        }

        @Test
        @DisplayName("violación de integridad: error con prefijo")
        void violacionIntegridad() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(record));
            when(medicalImageRepository.findByClinicalRecordId(10)).thenReturn(List.of());
            doThrow(new DataIntegrityViolationException("fk")).when(clinicalRecordRepository).delete(record);

            ApiResponse<Void> r = service.delete(10);

            assertThat(r.getMessage()).isEqualTo("Violacion de integridad en la base de datos: fk");
        }
    }

    @Nested
    @DisplayName("changePatient")
    class ChangePatient {

        @Test
        @DisplayName("registro inexistente: error")
        void registroInexistente() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.empty());

            assertThat(service.changePatient(10, 2).getMessage()).isEqualTo("Clinical Record no encontrado con ID: 10");
        }

        @Test
        @DisplayName("paciente nuevo inexistente: error sin guardar")
        void pacienteInexistente() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(record));
            when(patientRepository.findById(2)).thenReturn(Optional.empty());

            ApiResponse<ClinicalRecordResponseDTO> r = service.changePatient(10, 2);

            assertThat(r.getMessage()).isEqualTo("Paciente no encontrado con ID: 2");
            verify(clinicalRecordRepository, never()).save(any());
        }

        @Test
        @DisplayName("mismo paciente: error sin guardar")
        void mismoPaciente() {
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(record));
            when(patientRepository.findById(1)).thenReturn(Optional.of(patient));

            ApiResponse<ClinicalRecordResponseDTO> r = service.changePatient(10, 1);

            assertThat(r.getMessage()).isEqualTo("El paciente nuevo no puede ser el mismo que el paciente actual");
            verify(clinicalRecordRepository, never()).save(any());
        }

        @Test
        @DisplayName("válido: reasigna el paciente y guarda")
        void valido() {
            Patient otro = Patient.builder().id(2).build();
            when(clinicalRecordRepository.findById(10)).thenReturn(Optional.of(record));
            when(patientRepository.findById(2)).thenReturn(Optional.of(otro));
            when(clinicalRecordRepository.save(record)).thenReturn(record);
            when(mapper.toResponseDTO(record)).thenReturn(responseDTO);

            ApiResponse<ClinicalRecordResponseDTO> r = service.changePatient(10, 2);

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Paciente del registro médico cambiado exitosamente");
            assertThat(record.getPatient()).isSameAs(otro);
        }
    }
}
