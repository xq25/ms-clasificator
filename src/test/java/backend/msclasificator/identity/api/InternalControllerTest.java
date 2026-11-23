package backend.msclasificator.identity.api;

import backend.msclasificator.doctor.api.dto.DoctorCreateDTO;
import backend.msclasificator.doctor.application.DoctorService;
import backend.msclasificator.identity.application.exception.SecurityServiceException;
import backend.msclasificator.patient.api.dto.PatientCreateDTO;
import backend.msclasificator.patient.application.PatientService;
import backend.msclasificator.shared.api.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Los endpoints de registro los llama ms-security y esperan un boolean (bug #9, ADR-009)
@ExtendWith(MockitoExtension.class)
class InternalControllerTest {

    @Mock private DoctorService doctorService;
    @Mock private PatientService patientService;

    @InjectMocks private InternalController controller;

    private final PatientCreateDTO patientDto = PatientCreateDTO.builder().userId("u1").build();
    private final DoctorCreateDTO doctorDto = DoctorCreateDTO.builder().userId("u2").build();

    @Test
    @DisplayName("registerPatient: alta correcta → true")
    void registerPatientOk() {
        when(patientService.create(patientDto)).thenReturn(ApiResponse.success("ok"));

        assertThat(controller.registerPatient(patientDto)).isTrue();
    }

    @Test
    @DisplayName("registerPatient: error de validación → false")
    void registerPatientError() {
        when(patientService.create(patientDto)).thenReturn(ApiResponse.error("duplicado"));

        assertThat(controller.registerPatient(patientDto)).isFalse();
    }

    @Test
    @DisplayName("registerPatient: fallo de ms-security → false (no 503)")
    void registerPatientFalloSeguridad() {
        when(patientService.create(patientDto)).thenThrow(new SecurityServiceException("rol no asignado"));

        assertThat(controller.registerPatient(patientDto)).isFalse();
    }

    @Test
    @DisplayName("registerDoctor: fallo de ms-security → false (no 503)")
    void registerDoctorFalloSeguridad() {
        when(doctorService.create(doctorDto)).thenThrow(new SecurityServiceException("rol no asignado"));

        assertThat(controller.registerDoctor(doctorDto)).isFalse();
    }
}
