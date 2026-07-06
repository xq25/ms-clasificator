package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetCreateDTO;
import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetResponseDTO;
import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetSummaryDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import Backend.ms_clasificator.Services.DiagnosticCategoryDatasetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnostic-category-datasets")
public class DiagnosticCategoryDatasetController {

    @Autowired
    private DiagnosticCategoryDatasetService diagnosticCategoryDatasetService;

    /**
     * Obtener todas las asociaciones
     * @return Lista de asociaciones diagnóstico-categoría
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<DiagnosticCategoryDatasetSummaryDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {

        ApiResponse<PagedResponse<DiagnosticCategoryDatasetSummaryDTO>> response =
                this.diagnosticCategoryDatasetService.findAll(pageRequest);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(this.diagnosticCategoryDatasetService.count());
    }

    /**
     * Obtener una asociación por ID
     * @param id ID de la asociación
     * @return Asociación encontrada
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<DiagnosticCategoryDatasetResponseDTO>> findById(
            @PathVariable Integer id) {

        ApiResponse<DiagnosticCategoryDatasetResponseDTO> response =
                this.diagnosticCategoryDatasetService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener todos los diagnósticos asociados a una categoría
     * @param datasetCategoryId ID de la categoría
     * @return Lista de diagnósticos asociados
     */
    @GetMapping("dataset-category/{datasetCategoryId}")
    public ResponseEntity<ApiResponse<List<DiagnosticCategoryDatasetSummaryDTO>>> findByDatasetCategoryId(@PathVariable Integer datasetCategoryId) {

        ApiResponse<List<DiagnosticCategoryDatasetSummaryDTO>> response =
                this.diagnosticCategoryDatasetService.findByDatasetCategoryId(datasetCategoryId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Crear una nueva asociación diagnóstico-categoría
     * @param dto DTO de creación
     * @return Asociación creada
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<DiagnosticCategoryDatasetResponseDTO>> create(@Valid @RequestBody DiagnosticCategoryDatasetCreateDTO dto) {

        ApiResponse<DiagnosticCategoryDatasetResponseDTO> response =
                this.diagnosticCategoryDatasetService.create(dto);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar una asociación diagnóstico-categoría
     * @param id ID de la asociación
     * @return Resultado de la operación
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {

        ApiResponse<Void> response =
                this.diagnosticCategoryDatasetService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
