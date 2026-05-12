package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.UIState.CreateUIStateDTO;
import Backend.ms_clasificator.DTOs.UIState.UpdateUIStateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.UIState;
import Backend.ms_clasificator.Services.UIStateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ui-states")
public class UIStateController {

    @Autowired
    private UIStateService uiStateService;

    /**
     * Obtener todos los estados UI
     * @return Lista de todos los estados
     */
    @GetMapping("")
    public ResponseEntity<List<UIState>> findAll() {
        List<UIState> states = this.uiStateService.findAll();
        return ResponseEntity.ok(states);
    }

    /**
     * Obtener un estado UI por ID
     * @param id ID del estado
     * @return Estado encontrado
     */
    @GetMapping("{id}")
    public ResponseEntity<UIState> findById(@PathVariable Integer id) {
        UIState state = this.uiStateService.findById(id);
        if (state != null) {
            return ResponseEntity.ok(state);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener todos los estados UI de una configuración
     * @param uiConfigId ID de la configuración
     * @return Lista de estados de la configuración
     */
    @GetMapping("config/{uiConfigId}")
    public ResponseEntity<List<UIState>> findByUiConfigId(@PathVariable Integer uiConfigId) {
        List<UIState> states = this.uiStateService.findByUiConfigId(uiConfigId);
        return ResponseEntity.ok(states);
    }

    /**
     * Obtener todos los estados UI de un diagnóstico
     * @param medicalDiagnosticId ID del diagnóstico
     * @return Lista de estados del diagnóstico
     */
    @GetMapping("diagnostic/{medicalDiagnosticId}")
    public ResponseEntity<List<UIState>> findByMedicalDiagnosticId(@PathVariable Integer medicalDiagnosticId) {
        List<UIState> states = this.uiStateService.findByMedicalDiagnosticId(medicalDiagnosticId);
        return ResponseEntity.ok(states);
    }

    /**
     * Crear un nuevo estado UI
     * @param createUIStateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<UIState>> create(@Valid @RequestBody CreateUIStateDTO createUIStateDTO) {
        ApiResponse<UIState> response = this.uiStateService.create(createUIStateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar un estado UI existente
     * @param id ID del estado a actualizar
     * @param updateUIStateDTO DTO con datos a actualizar
     * @return ApiResponse con el resultado
     */
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<UIState>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUIStateDTO updateUIStateDTO) {
        ApiResponse<UIState> response = this.uiStateService.update(id, updateUIStateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar un estado UI
     * @param id ID del estado a eliminar
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = this.uiStateService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
