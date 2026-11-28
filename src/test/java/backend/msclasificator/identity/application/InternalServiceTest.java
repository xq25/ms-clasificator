package backend.msclasificator.identity.application;

import backend.msclasificator.doctor.application.port.out.DoctorAreaRepository;
import backend.msclasificator.doctor.application.port.out.DoctorRepository;
import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.patient.application.port.out.PatientRepository;
import backend.msclasificator.patient.domain.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Consultas que ms-security hace sobre las relaciones de un usuario (endpoints internos)
@ExtendWith(MockitoExtension.class)
class InternalServiceTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DoctorAreaRepository doctorAreaRepository;

    @InjectMocks private InternalService service;

    private static final String USER_ID = "user-1";

    @Nested
    @DisplayName("existRelation")
    class ExistRelation {

        @Test
        @DisplayName("es doctor: true")
        void doctor() {
            when(doctorRepository.findByUserId(USER_ID)).thenReturn(Optional.of(new Doctor()));
            when(patientRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

            assertThat(service.existRelation(USER_ID)).isTrue();
        }

        @Test
        @DisplayName("es paciente: true")
        void paciente() {
            when(doctorRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
            when(patientRepository.findByUserId(USER_ID)).thenReturn(Optional.of(new Patient()));

            assertThat(service.existRelation(USER_ID)).isTrue();
        }

        @Test
        @DisplayName("sin relación: false")
        void ninguna() {
            when(doctorRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
            when(patientRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

            assertThat(service.existRelation(USER_ID)).isFalse();
        }
    }

    @Nested
    @DisplayName("existRelationWithDoctor / existRelationWithPatient")
    class PorRol {

        @Test
        @DisplayName("doctor existente: true; inexistente: false")
        void doctor() {
            when(doctorRepository.findByUserId(USER_ID)).thenReturn(Optional.of(new Doctor()));
            when(doctorRepository.findByUserId("otro")).thenReturn(Optional.empty());

            assertThat(service.existRelationWithDoctor(USER_ID)).isTrue();
            assertThat(service.existRelationWithDoctor("otro")).isFalse();
        }

        @Test
        @DisplayName("paciente existente: true; inexistente: false")
        void paciente() {
            when(patientRepository.findByUserId(USER_ID)).thenReturn(Optional.of(new Patient()));
            when(patientRepository.findByUserId("otro")).thenReturn(Optional.empty());

            assertThat(service.existRelationWithPatient(USER_ID)).isTrue();
            assertThat(service.existRelationWithPatient("otro")).isFalse();
        }
    }

    @Nested
    @DisplayName("existsDoctorInArea")
    class DoctorEnArea {

        @Test
        @DisplayName("delega en DoctorAreaRepository")
        void delega() {
            when(doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(2, 1)).thenReturn(true);
            when(doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(2, 3)).thenReturn(false);

            assertThat(service.existsDoctorInArea(2, 1)).isTrue();
            assertThat(service.existsDoctorInArea(2, 3)).isFalse();
        }
    }
}
