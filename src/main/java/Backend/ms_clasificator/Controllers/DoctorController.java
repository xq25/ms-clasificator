package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.Doctor.DoctorBaseDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorSummaryDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Doctor.DoctorResponseDTO;
import Backend.ms_clasificator.Services.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<DoctorSummaryDTO>>> find(){
        ApiResponse<List<DoctorSummaryDTO>> response = this.doctorService.findAll();
        if (response.isSuccess()) return ResponseEntity.ok(response);
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> findById(@PathVariable Integer id){
        ApiResponse<DoctorResponseDTO> response = this.doctorService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("user-id/{userId}")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> findByUserId(@PathVariable String userId){
        ApiResponse<DoctorResponseDTO> response = this.doctorService.findByUserId(userId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("code/{code}")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> findByCode(@PathVariable String code){
        ApiResponse<DoctorResponseDTO> response = this.doctorService.findByCode(code);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<DoctorSummaryDTO>> create(@Valid @RequestBody DoctorBaseDTO doctor) {
        ApiResponse<DoctorSummaryDTO> response = this.doctorService.create(doctor);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<DoctorSummaryDTO>> update(@PathVariable Integer id, @Valid @RequestBody DoctorUpdateDTO doctor) {
        ApiResponse<DoctorSummaryDTO> response = this.doctorService.update(id, doctor);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id){
        ApiResponse<Void> response = this.doctorService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }

    }

}
