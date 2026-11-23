package backend.msclasificator.identity.api;

import backend.msclasificator.doctor.api.dto.DoctorCreateDTO;
import backend.msclasificator.doctor.api.dto.DoctorSummaryDTO;
import backend.msclasificator.patient.api.dto.PatientCreateDTO;
import backend.msclasificator.patient.api.dto.PatientSummaryDTO;
import backend.msclasificator.identity.application.exception.SecurityServiceException;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.doctor.application.DoctorService;
import backend.msclasificator.identity.application.InternalService;
import backend.msclasificator.patient.application.PatientService;
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

    // ms-security espera un boolean: si el alta falla por ms-security (ya revertida en el servicio),
    // se responde false en lugar del 503 del GlobalExceptionHandler.
    @PostMapping("register/doctor")
    public boolean registerDoctor(@RequestBody DoctorCreateDTO doctorBaseDTO){
        try {
            ApiResponse<DoctorSummaryDTO> registerResponse = this.doctorService.create(doctorBaseDTO);
            return registerResponse.isSuccess();
        } catch (SecurityServiceException ex) {
            return false;
        }
    }

    @PostMapping("register/patient")
    public boolean registerPatient(@RequestBody PatientCreateDTO patientCreateDTO){
        try {
            ApiResponse<PatientSummaryDTO> registerResponse = this.patientService.create(patientCreateDTO);
            return registerResponse.isSuccess();
        } catch (SecurityServiceException ex) {
            return false;
        }
    }
}
