package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticResponseDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticSummaryDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticUpdateDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
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

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<MedicalDiagnosticResponseDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<MedicalDiagnosticResponseDTO>> response = medicalDiagnosticService.findAll(pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("search/by-name")
    public ResponseEntity<ApiResponse<PagedResponse<MedicalDiagnosticSummaryDTO>>> searchByName(
            @RequestParam String query,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<MedicalDiagnosticSummaryDTO>> response = medicalDiagnosticService.searchByName(query, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("search/by-code")
    public ResponseEntity<ApiResponse<PagedResponse<MedicalDiagnosticSummaryDTO>>> searchByCode(
            @RequestParam String query,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<MedicalDiagnosticSummaryDTO>> response = medicalDiagnosticService.searchByCode(query, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(medicalDiagnosticService.count());
    }

    /**
     * Obtener un diagnóstico médico por ID
     * @param id ID del diagnóstico
     * @return Diagnóstico encontrado
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<MedicalDiagnosticResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<MedicalDiagnosticResponseDTO> response = this.medicalDiagnosticService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("parentId/{parentId}")
    public ResponseEntity<ApiResponse<List<MedicalDiagnosticSummaryDTO>>> findByParentId(@PathVariable Integer parentId) {
        ApiResponse<List<MedicalDiagnosticSummaryDTO>> response = this.medicalDiagnosticService.findByParentDiagnosticId(parentId);

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
    public ResponseEntity<ApiResponse<MedicalDiagnosticResponseDTO>> create(@Valid @RequestBody MedicalDiagnosticCreateDTO medicalDiagnosticCreateDTO) {
        ApiResponse<MedicalDiagnosticResponseDTO> response = medicalDiagnosticService.create(medicalDiagnosticCreateDTO);

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
    public ResponseEntity<ApiResponse<MedicalDiagnosticResponseDTO>> update(@PathVariable Integer id, @Valid @RequestBody MedicalDiagnosticUpdateDTO medicalDiagnosticUpdateDTO) {
        ApiResponse<MedicalDiagnosticResponseDTO> response = medicalDiagnosticService.update(id, medicalDiagnosticUpdateDTO);

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
    public ResponseEntity<ApiResponse<MedicalDiagnosticResponseDTO>> addSubDiagnostic(@PathVariable Integer parentDiagnosticId, @PathVariable Integer subDiagnosticId) {
        ApiResponse<MedicalDiagnosticResponseDTO> response = medicalDiagnosticService.addSubDiagnostic(parentDiagnosticId, subDiagnosticId);

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
    public ResponseEntity<ApiResponse<MedicalDiagnosticResponseDTO>> removeSubDiagnostic(@PathVariable Integer parentDiagnosticId, @PathVariable Integer subDiagnosticId) {
        ApiResponse<MedicalDiagnosticResponseDTO> response = medicalDiagnosticService.removeSubDiagnostic(parentDiagnosticId, subDiagnosticId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
