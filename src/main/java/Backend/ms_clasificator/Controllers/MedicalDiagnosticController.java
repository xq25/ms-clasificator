package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Services.MedicalDiagnosticService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-diagnostics")
public class MedicalDiagnosticController {

    @Autowired
    private MedicalDiagnosticService medicalDiagnosticService;

    /**
     * Obtener todos los diagnósticos médicos
     * @return Lista de todos los diagnósticos
     */
    @GetMapping("")
    public ResponseEntity<List<MedicalDiagnostic>> findAll() {
        List<MedicalDiagnostic> diagnostics = medicalDiagnosticService.findAll();
        return ResponseEntity.ok(diagnostics);
    }

    /**
     * Obtener un diagnóstico médico por ID
     * @param id ID del diagnóstico
     * @return Diagnóstico encontrado
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<MedicalDiagnostic>> findById(@PathVariable Integer id) {
        ApiResponse<MedicalDiagnostic> response = this.medicalDiagnosticService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("parentId/{parentId}")
    public ResponseEntity<ApiResponse<List<MedicalDiagnostic>>> findByParentId(@PathVariable Integer parentId) {
        ApiResponse<List<MedicalDiagnostic>> response = this.medicalDiagnosticService.findByParentDiagnosticId(parentId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Crear un nuevo diagnóstico médico
     * @param medicalDiagnosticCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<MedicalDiagnostic>> create(@Valid @RequestBody MedicalDiagnosticCreateDTO medicalDiagnosticCreateDTO) {
        ApiResponse<MedicalDiagnostic> response = medicalDiagnosticService.create(medicalDiagnosticCreateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar un diagnóstico médico existente
     * @param id ID del diagnóstico a actualizar
     * @param medicalDiagnosticUpdateDTO DTO con datos a actualizar
     * @return ApiResponse con el resultado
     */
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<MedicalDiagnostic>> update(@PathVariable Integer id, @Valid @RequestBody MedicalDiagnosticUpdateDTO medicalDiagnosticUpdateDTO) {
        ApiResponse<MedicalDiagnostic> response = medicalDiagnosticService.update(id, medicalDiagnosticUpdateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar un diagnóstico médico
     * @param id ID del diagnóstico a eliminar
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = medicalDiagnosticService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ===== Relaciones con otras entidades (Relación padre-hijo) =====

    /**
     * Agregar un sub-diagnóstico a un diagnóstico padre
     * @param parentDiagnosticId ID del diagnóstico padre
     * @param subDiagnosticId ID del sub-diagnóstico
     * @return ApiResponse con el resultado
     */
    @PutMapping("{parentDiagnosticId}/add-sub-diagnostic/{subDiagnosticId}")
    public ResponseEntity<ApiResponse<MedicalDiagnostic>> addSubDiagnostic(@PathVariable Integer parentDiagnosticId, @PathVariable Integer subDiagnosticId) {
        ApiResponse<MedicalDiagnostic> response = medicalDiagnosticService.addSubDiagnostic(parentDiagnosticId, subDiagnosticId);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Remover un sub-diagnóstico de un diagnóstico padre
     * @param parentDiagnosticId ID del diagnóstico padre
     * @param subDiagnosticId ID del sub-diagnóstico
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{parentDiagnosticId}/remove-sub-diagnostic/{subDiagnosticId}")
    public ResponseEntity<ApiResponse<MedicalDiagnostic>> removeSubDiagnostic(@PathVariable Integer parentDiagnosticId, @PathVariable Integer subDiagnosticId) {
        ApiResponse<MedicalDiagnostic> response = medicalDiagnosticService.removeSubDiagnostic(parentDiagnosticId, subDiagnosticId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
