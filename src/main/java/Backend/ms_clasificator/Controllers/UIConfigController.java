package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.UIConfig.CreateUIConfigDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.UIConfig;
import Backend.ms_clasificator.Services.UIConfigService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ui-configs")
public class UIConfigController {

    @Autowired
    private UIConfigService uiConfigService;

    /**
     * Obtener todas las configuraciones UI
     * @return Lista de todas las configuraciones
     */
    @GetMapping("")
    public ResponseEntity<List<UIConfig>> findAll() {
        List<UIConfig> configs = uiConfigService.findAll();
        return ResponseEntity.ok(configs);
    }

    /**
     * Obtener una configuración UI por ID
     * @param id ID de la configuración
     * @return Configuración encontrada
     */
    @GetMapping("{id}")
    public ResponseEntity<UIConfig> findById(@PathVariable Integer id) {
        UIConfig config = uiConfigService.findById(id);
        if (config != null) {
            return ResponseEntity.ok(config);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener todas las configuraciones UI de un diagnóstico médico
     * @param medicalDiagnosticId ID del diagnóstico médico
     * @return Lista de configuraciones del diagnóstico
     */
    @GetMapping("diagnostic/{medicalDiagnosticId}")
    public ResponseEntity<List<UIConfig>> findByMedicalDiagnosticId(@PathVariable Integer medicalDiagnosticId) {
        List<UIConfig> configs = uiConfigService.findByMedicalDiagnosticId(medicalDiagnosticId);
        return ResponseEntity.ok(configs);
    }

    /**
     * Crear una nueva configuración UI
     * @param createUIConfigDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<UIConfig>> create(@Valid @RequestBody CreateUIConfigDTO createUIConfigDTO) {
        ApiResponse<UIConfig> response = uiConfigService.create(createUIConfigDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar una configuración UI existente
     * @param id ID de la configuración a actualizar
     * @param createUIConfigDTO DTO con datos a actualizar
     * @return ApiResponse con el resultado
     */
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<UIConfig>> update(@PathVariable Integer id, @Valid @RequestBody CreateUIConfigDTO createUIConfigDTO) {
        ApiResponse<UIConfig> response = uiConfigService.update(id, createUIConfigDTO);

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
        ApiResponse<Void> response = uiConfigService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
