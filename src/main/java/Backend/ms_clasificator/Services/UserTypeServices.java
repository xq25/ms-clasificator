package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Doctor.DoctorBaseDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientCreateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserTypeServices {
    @Value("${default.role.doctor}")
    private String DEFAULT_ROLE_DOCTOR;

    @Value("${default.role.patient}")
    private String DEFAULT_ROLE_PATIENT;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private SecurityServices securityServices;

    public ApiResponse<Doctor> assingDoctorRegister(DoctorBaseDTO doctorInfo){
        // Primero creamos la instancia de doctor asignando este userId
        ApiResponse<Doctor> newDoctor = this.doctorService.create(doctorInfo);

        // Asignamos el role Correspondiente a ese usuario.
        if (!newDoctor.isSuccess()){
            return ApiResponse.error("Error al crear doctor para register: " + newDoctor.getMessage());
        }
        boolean success = this.securityServices.assignDefaultRole(doctorInfo.getUserId(), DEFAULT_ROLE_DOCTOR);

        if (!success){
            return ApiResponse.error("Error al asignar rol por defecto al doctor recién creado con userId: " + doctorInfo.getUserId());
        }

        return newDoctor;
    }

    public ApiResponse<Patient> assignPatientRegister(PatientCreateDTO patientInfo){
        // Primero creamos la instancia de doctor asignando este userId
        ApiResponse<Patient> newDoctor = this.patientService.create(patientInfo);

        // Asignamos el role Correspondiente a ese usuario.
        if (!newDoctor.isSuccess()){
            return ApiResponse.error("Error al crear patient para register: " + newDoctor.getMessage());
        }
        boolean success = this.securityServices.assignDefaultRole(patientInfo.getUserId(), DEFAULT_ROLE_PATIENT);

        if (!success){
            return ApiResponse.error("Error al asignar rol por defecto al patient recién creado con userId: " + patientInfo.getUserId());
        }

        return newDoctor;
    }
}
