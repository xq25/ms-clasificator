package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaCreateDTO;
import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaSummaryDTO;
import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaUpdateDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaResponseDTO;
import Backend.ms_clasificator.Services.EvaluationAreaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-areas")
public class EvaluationAreaController {

    @Autowired
    private EvaluationAreaService evaluationAreaService;

    /**
     * Obtener todas las áreas de evaluación
     * @return Lista de todas las áreas de evaluación
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<EvaluationAreaResponseDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<EvaluationAreaResponseDTO>> response = evaluationAreaService.findAll(pageRequest);
        if (response.isSuccess()) return ResponseEntity.ok(response);
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("search/by-name")
    public ResponseEntity<ApiResponse<PagedResponse<EvaluationAreaSummaryDTO>>> searchByName(
            @RequestParam String query,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<EvaluationAreaSummaryDTO>> response = evaluationAreaService.searchByName(query, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("search/by-code")
    public ResponseEntity<ApiResponse<PagedResponse<EvaluationAreaSummaryDTO>>> searchByCode(
            @RequestParam String query,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<EvaluationAreaSummaryDTO>> response = evaluationAreaService.searchByCode(query, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(evaluationAreaService.count());
    }

    /**
     * Obtener un área de evaluación por ID
     * @param id ID del área
     * @return Área encontrada
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<EvaluationAreaResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<EvaluationAreaResponseDTO> response = evaluationAreaService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Crear una nueva área de evaluación
     * @param evaluationAreaCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<EvaluationAreaResponseDTO>> create(@Valid @RequestBody EvaluationAreaCreateDTO evaluationAreaCreateDTO) {
        ApiResponse<EvaluationAreaResponseDTO> response = evaluationAreaService.create(evaluationAreaCreateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar un área de evaluación existente
     * @param id ID del área a actualizar
     * @param evaluationAreaUpdateDTO DTO con datos a actualizar
     * @return ApiResponse con el resultado
     */
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<EvaluationAreaResponseDTO>> update(@PathVariable Integer id, @Valid @RequestBody EvaluationAreaUpdateDTO evaluationAreaUpdateDTO) {
        ApiResponse<EvaluationAreaResponseDTO> response = evaluationAreaService.update(id, evaluationAreaUpdateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar un área de evaluación
     * @param id ID del área a eliminar
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = evaluationAreaService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

}
