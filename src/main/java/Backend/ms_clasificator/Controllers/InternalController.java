package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.Doctor.DoctorBaseDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorSummaryDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientCreateDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientSummaryDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Services.DoctorService;
import Backend.ms_clasificator.Services.InternalService;
import Backend.ms_clasificator.Services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/internal")
public class InternalController {
    @Autowired
    private InternalService internalService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @GetMapping("exist-relation/{userId}")
    public boolean existRelationWithDoctorOrPatient(@PathVariable String userId){
        return this.internalService.existRelation(userId);
    }

    @GetMapping("exist-relation-doctor/{userId}")
    public boolean existRelationWithDoctor(@PathVariable String userId){
        return this.internalService.existRelationWithDoctor(userId);
    }

    @GetMapping("exist-relation-patient/{userId}")
    public boolean existRelationWithPatient(@PathVariable String userId){
        return this.internalService.existRelationWithPatient(userId);
    }

    @GetMapping("exists-doctor-in-area/{doctorId}/{evaluationAreaId}")
    public boolean existsDoctorInArea(@PathVariable Integer doctorId, @PathVariable Integer evaluationAreaId){
        return this.internalService.existsDoctorInArea(doctorId, evaluationAreaId);
    }

    @PostMapping("register/doctor")
    public boolean registerDoctor(@RequestBody DoctorBaseDTO doctorBaseDTO){
        ApiResponse<DoctorSummaryDTO> registerResponse =  this.doctorService.create(doctorBaseDTO);
        return registerResponse.isSuccess();
    }

    @PostMapping("register/patient")
    public boolean registerPatient(@RequestBody PatientCreateDTO patientCreateDTO){
        ApiResponse<PatientSummaryDTO> registerResponse = this.patientService.create(patientCreateDTO);

        return registerResponse.isSuccess();
    }
}
