package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaCreateDTO;
import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.DoctorArea;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Services.EvaluationAreaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation-areas")
public class EvaluationAreaController {

    @Autowired
    private EvaluationAreaService evaluationAreaService;

    /**
     * Obtener todas las áreas de evaluación
     * @return Lista de todas las áreas de evaluación
     */
    @GetMapping("")
    public ResponseEntity<List<EvaluationArea>> findAll() {
        List<EvaluationArea> areas = evaluationAreaService.findAll();
        return ResponseEntity.ok(areas);
    }

    /**
     * Obtener un área de evaluación por ID
     * @param id ID del área
     * @return Área encontrada
     */
    @GetMapping("{id}")
    public ResponseEntity<EvaluationArea> findById(@PathVariable Integer id) {
        EvaluationArea area = evaluationAreaService.findById(id);
        if (area != null) {
            return ResponseEntity.ok(area);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crear una nueva área de evaluación
     * @param evaluationAreaCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<EvaluationArea>> create(@Valid @RequestBody EvaluationAreaCreateDTO evaluationAreaCreateDTO) {
        ApiResponse<EvaluationArea> response = evaluationAreaService.create(evaluationAreaCreateDTO);

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
    public ResponseEntity<ApiResponse<EvaluationArea>> update(@PathVariable Integer id, @Valid @RequestBody EvaluationAreaUpdateDTO evaluationAreaUpdateDTO) {
        ApiResponse<EvaluationArea> response = evaluationAreaService.update(id, evaluationAreaUpdateDTO);

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
