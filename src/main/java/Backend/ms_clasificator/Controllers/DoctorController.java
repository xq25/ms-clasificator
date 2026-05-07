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

    @GetMapping("{code}")
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
    public void delete(@PathVariable Integer id){ this.doctorService.delete(id);}

    // ===== Relaciones con otras entidades =====

    /**
     * Asociar un doctor a un área de evaluación
     * @param doctorId ID del doctor
     * @param evaluationAreaId ID del área de evaluación
     * @return ApiResponse con el resultado
     */
    @PostMapping("{doctorId}/join-area/{evaluationAreaId}")
    public ResponseEntity<ApiResponse<DoctorArea>> joinInEvaluationArea(@PathVariable Integer doctorId, @PathVariable Integer evaluationAreaId) {
        ApiResponse<DoctorArea> response = this.doctorService.joinInEvaluationArea(doctorId, evaluationAreaId);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Disociar un doctor de un área de evaluación
     * @param doctorId ID del doctor
     * @param evaluationAreaId ID del área de evaluación
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{doctorId}/remove-area/{evaluationAreaId}")
    public ResponseEntity<ApiResponse<Void>> removeFromEvaluationArea(@PathVariable Integer doctorId, @PathVariable Integer evaluationAreaId) {
        ApiResponse<Void> response = this.doctorService.removeFromEvaluationArea(doctorId, evaluationAreaId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
