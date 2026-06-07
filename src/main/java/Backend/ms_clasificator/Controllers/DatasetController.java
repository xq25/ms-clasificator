package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.Dataset.DatasetCreateDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetResponseDTO;
import Backend.ms_clasificator.DTOs.Dataset.DatasetSummaryDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Dataset.DatasetUpdateDTO;
import Backend.ms_clasificator.Services.DatasetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    @Autowired
    private DatasetService datasetService;

    /**
     * Obtener todas las configuraciones UI
     * @return Lista de todas las configuraciones
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<DatasetSummaryDTO>>> findAll() {
        ApiResponse<List<DatasetSummaryDTO>> response = datasetService.findAll();
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    /**
     * Obtener una configuración UI por ID
     * @param id ID de la configuración
     * @return Configuración encontrada
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<DatasetResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<DatasetResponseDTO> response = this.datasetService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener todas las configuraciones UI de un diagnóstico médico
     * @param medicalDiagnosticId ID del diagnóstico médico
     * @return Lista de configuraciones del diagnóstico
     */
    @GetMapping("diagnostic/{medicalDiagnosticId}")
    public ResponseEntity<ApiResponse<List<DatasetResponseDTO>>> findByMedicalDiagnosticId(@PathVariable Integer medicalDiagnosticId) {
        ApiResponse<List<DatasetResponseDTO>> response = this.datasetService.findByMedicalDiagnosticId(medicalDiagnosticId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("area/{evaluationAreaId}")
    public ResponseEntity<ApiResponse<DatasetSummaryDTO>> findByEvaluationAreaId(@PathVariable Integer evaluationAreaId){
        ApiResponse<DatasetSummaryDTO> response = this.datasetService.findByEvaluationAreaId(evaluationAreaId);

        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Crear una nueva configuración UI
     * @param datasetCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<DatasetResponseDTO>> create(@Valid @RequestBody DatasetCreateDTO datasetCreateDTO) {
        ApiResponse<DatasetResponseDTO> response = datasetService.create(datasetCreateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar una configuración UI existente
     * @param id ID de la configuración a actualizar
     * @param datasetUpdateDTO DTO con datos a actualizar
     * @return ApiResponse con el resultado
     */
    @PutMapping("{id}/change-diagnostic")
    public ResponseEntity<ApiResponse<DatasetResponseDTO>> update(@PathVariable Integer id, @Valid @RequestBody DatasetUpdateDTO datasetUpdateDTO) {
        ApiResponse<DatasetResponseDTO> response = datasetService.update(id, datasetUpdateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar una configuración UI
     * @param id ID de la configuración a eliminar
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = datasetService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("{uiConfigId}/evaluation-area/{evaluationAreaId}")
    public ResponseEntity<ApiResponse<DatasetResponseDTO>> assignEvaluationArea(@PathVariable Integer uiConfigId, @PathVariable Integer evaluationAreaId) {
        ApiResponse<DatasetResponseDTO> response = datasetService.assingToEvaluationArea(uiConfigId, evaluationAreaId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("removeFromArea/{uiConfigId}")
    public ResponseEntity<ApiResponse<DatasetResponseDTO>> removeFromEvaluationArea(@PathVariable Integer uiConfigId) {
        ApiResponse<DatasetResponseDTO> response = datasetService.removeFromEvaluationArea(uiConfigId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

}
