package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.Doctor.DoctorBaseDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.DoctorArea;
import Backend.ms_clasificator.Services.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("")
    public List<Doctor> find(){ return this.doctorService.findAll();}

    @GetMapping("{id}")
    public Doctor findById(@PathVariable Integer id){ return this.doctorService.findById(id);}

    @GetMapping("code/{code}")
    public Doctor findByCode(@PathVariable String code){ return this.doctorService.findByCode(code);}

    @PostMapping("")
    public ResponseEntity<ApiResponse<Doctor>> create(@Valid @RequestBody DoctorBaseDTO doctor) {
        ApiResponse<Doctor> response = this.doctorService.create(doctor);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<Doctor>> update(@PathVariable Integer id, @Valid @RequestBody DoctorUpdateDTO doctor) {
        ApiResponse<Doctor> response = this.doctorService.update(id, doctor);

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
