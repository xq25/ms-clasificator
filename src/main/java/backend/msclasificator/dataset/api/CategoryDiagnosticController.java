package backend.msclasificator.dataset.api;

import backend.msclasificator.dataset.api.dto.CategoryDiagnosticCreateDTO;
import backend.msclasificator.dataset.api.dto.CategoryDiagnosticResponseDTO;
import backend.msclasificator.dataset.api.dto.CategoryDiagnosticSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.dataset.application.CategoryDiagnosticService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnostic-category-datasets")
public class CategoryDiagnosticController {

    @Autowired
    private CategoryDiagnosticService diagnosticCategoryDatasetService;

    /**
     * Obtener todas las asociaciones
     * @return Lista de asociaciones diagnóstico-categoría
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<CategoryDiagnosticSummaryDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {

        ApiResponse<PagedResponse<CategoryDiagnosticSummaryDTO>> response =
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
    public ResponseEntity<ApiResponse<CategoryDiagnosticResponseDTO>> findById(
            @PathVariable Integer id) {

        ApiResponse<CategoryDiagnosticResponseDTO> response =
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
    public ResponseEntity<ApiResponse<List<CategoryDiagnosticSummaryDTO>>> findByDatasetCategoryId(@PathVariable Integer datasetCategoryId) {

        ApiResponse<List<CategoryDiagnosticSummaryDTO>> response =
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
    public ResponseEntity<ApiResponse<CategoryDiagnosticResponseDTO>> create(@Valid @RequestBody CategoryDiagnosticCreateDTO dto) {

        ApiResponse<CategoryDiagnosticResponseDTO> response =
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
