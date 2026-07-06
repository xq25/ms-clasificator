package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.DoctorArea.DoctorAreaCreateDTO;
import Backend.ms_clasificator.DTOs.DoctorArea.DoctorAreaResponseDTO;
import Backend.ms_clasificator.DTOs.DoctorArea.DoctorAreaSummaryDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import Backend.ms_clasificator.Services.DoctorAreaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-areas")
public class DoctorAreaController {

    @Autowired
    private DoctorAreaService doctorAreaService;

    /**
     * Obtener todas las relaciones DoctorArea
     * @return Lista de todas las relaciones
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<DoctorAreaSummaryDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<DoctorAreaSummaryDTO>> response = this.doctorAreaService.findAll(pageRequest);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(this.doctorAreaService.count());
    }

    /**
     * Obtener una relación DoctorArea por ID
     * @param id ID de la relación
     * @return DoctorArea encontrada
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<DoctorAreaResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<DoctorAreaResponseDTO> response = this.doctorAreaService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener las áreas de evaluación de un doctor específico
     * @param doctorId ID del doctor
     * @return Lista de DoctorArea del doctor
     */
    @GetMapping("doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<DoctorAreaSummaryDTO>>> findByDoctorId(@PathVariable Integer doctorId) {
        ApiResponse<List<DoctorAreaSummaryDTO>> response = this.doctorAreaService.findByDoctorId(doctorId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener los doctores de un área de evaluación específica
     * @param evaluationAreaId ID del área de evaluación
     * @return Lista de DoctorArea en el área
     */
    @GetMapping("area/{evaluationAreaId}")
    public ResponseEntity<ApiResponse<List<DoctorAreaSummaryDTO>>> findByEvaluationAreaId(@PathVariable Integer evaluationAreaId) {
        ApiResponse<List<DoctorAreaSummaryDTO>> response = this.doctorAreaService.findByEvaluationAreaId(evaluationAreaId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Crear una nueva relación DoctorArea
     * @param doctorAreaCreateDTO DTO con los datos de la relación
     * @return ResponseEntity con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<DoctorAreaResponseDTO>> create(@Valid @RequestBody DoctorAreaCreateDTO doctorAreaCreateDTO) {
        ApiResponse<DoctorAreaResponseDTO> response = this.doctorAreaService.create(doctorAreaCreateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }

    }

    /**
     * Eliminar una relación DoctorArea por ID
     * @param id ID de la relación a eliminar
     * @return ResponseEntity con el resultado
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = this.doctorAreaService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar la relación entre un doctor y un área de evaluación
     * @param doctorId ID del doctor
     * @param evaluationAreaId ID del área de evaluación
     * @return ResponseEntity con el resultado
     */
    @DeleteMapping("doctor/{doctorId}/area/{evaluationAreaId}")
    public ResponseEntity<ApiResponse<Void>> deleteByDoctorAndArea(@PathVariable Integer doctorId, @PathVariable Integer evaluationAreaId) {
        ApiResponse<Void> response = this.doctorAreaService.deleteByDoctorAndArea(doctorId, evaluationAreaId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}

