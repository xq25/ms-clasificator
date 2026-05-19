package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.Doctor.DoctorBaseDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientCreateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.Patient;
import Backend.ms_clasificator.Services.UserTypeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/public/user-type")
@RestController
public class UserTypeController {
    @Autowired
    private UserTypeServices userTypeServices;

    @PutMapping("/doctor/{userId}")
    public ResponseEntity<ApiResponse<Doctor>> assignDoctorRegister(DoctorBaseDTO doctorInfo){
        ApiResponse<Doctor> response = this.userTypeServices.assingDoctorRegister(doctorInfo);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/patient/{userId}")
    public ResponseEntity<ApiResponse<Patient>> assignPatientRegister(PatientCreateDTO patientInfo){
        ApiResponse<Patient> response = this.userTypeServices.assignPatientRegister(patientInfo);
        if(response.isSuccess()){
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
