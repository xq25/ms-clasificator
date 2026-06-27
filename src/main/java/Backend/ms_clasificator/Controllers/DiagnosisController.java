package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.Diagnosis.DiagnosisCreateDTO;
import Backend.ms_clasificator.DTOs.Diagnosis.DiagnosisResponseDTO;
import Backend.ms_clasificator.DTOs.Diagnosis.DiagnosisSummaryDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import Backend.ms_clasificator.Services.DiagnosisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diagnosis")
public class DiagnosisController {

    @Autowired
    private DiagnosisService diagnosisService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<DiagnosisSummaryDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<DiagnosisSummaryDTO>> response = diagnosisService.findAll(pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(diagnosisService.count());
    }

    /**
     * Obtener un diagnóstico por ID
     * @param id ID del diagnóstico
     * @return Diagnóstico encontrado
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<DiagnosisResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<DiagnosisResponseDTO> response = diagnosisService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("clinical-record/{clinicalRecordId}")
    public ResponseEntity<ApiResponse<PagedResponse<DiagnosisSummaryDTO>>> findByClinicalRecordId(
            @PathVariable Integer clinicalRecordId,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<DiagnosisSummaryDTO>> response = diagnosisService.findByClinicalRecordId(clinicalRecordId, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    /**
     * Crear un nuevo diagnóstico
     * @param diagnosisCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<DiagnosisResponseDTO>> create(@Valid @RequestBody DiagnosisCreateDTO diagnosisCreateDTO) {
        ApiResponse<DiagnosisResponseDTO> response = diagnosisService.create(diagnosisCreateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // No existe metodo de actualizacion, ya que es una asociacion

    /**
     * Eliminar un diagnóstico
     * @param id ID del diagnóstico a eliminar
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = diagnosisService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

}
