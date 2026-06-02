package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.Diagnosis.DiagnosisCreateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.Diagnosis;
import Backend.ms_clasificator.Services.DiagnosisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosis")
public class DiagnosisController {

    @Autowired
    private DiagnosisService diagnosisService;

    /**
     * Obtener todos los diagnósticos
     * @return Lista de todos los diagnósticos
     */
    @GetMapping("")
    public ResponseEntity<List<Diagnosis>> findAll() {
        List<Diagnosis> diagnoses = diagnosisService.findAll();
        return ResponseEntity.ok(diagnoses);
    }

    /**
     * Obtener un diagnóstico por ID
     * @param id ID del diagnóstico
     * @return Diagnóstico encontrado
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<Diagnosis>> findById(@PathVariable Integer id) {
        ApiResponse<Diagnosis> response = diagnosisService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener los diagnósticos de una visita médica (Clinical Record)
     * @param clinicalRecordId ID del Clinical Record
     * @return Diagnósticos encontrados para la visita
     */
    @GetMapping("clinical-record/{clinicalRecordId}")
    public ResponseEntity<ApiResponse<Diagnosis>> findByClinicalRecordId(@PathVariable Integer clinicalRecordId) {
        ApiResponse<Diagnosis> response = diagnosisService.findByClinicalRecordId(clinicalRecordId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Crear un nuevo diagnóstico
     * @param diagnosisCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<Diagnosis>> create(@Valid @RequestBody DiagnosisCreateDTO diagnosisCreateDTO) {
        ApiResponse<Diagnosis> response = diagnosisService.create(diagnosisCreateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

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
