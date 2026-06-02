package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.ImageDiagnostic;
import Backend.ms_clasificator.Services.ImageDiagnosticService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/image-diagnostics")
public class ImageDiagnosticController {

    @Autowired
    private ImageDiagnosticService imageDiagnosticService;

    /**
     * Obtener todos los diagnósticos de imagen
     * @return Lista de todos los diagnósticos
     */
    @GetMapping("")
    public ResponseEntity<List<ImageDiagnostic>> findAll() {
        List<ImageDiagnostic> diagnostics = imageDiagnosticService.findAll();
        return ResponseEntity.ok(diagnostics);
    }

    /**
     * Obtener un diagnóstico de imagen por ID
     * @param id ID del diagnóstico
     * @return Diagnóstico encontrado
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<ImageDiagnostic>> findById(@PathVariable Integer id) {
        ApiResponse<ImageDiagnostic> response = imageDiagnosticService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Crear un nuevo diagnóstico de imagen
     * @param imageDiagnosticCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<ImageDiagnostic>> create(@Valid @RequestBody ImageDiagnosticCreateDTO imageDiagnosticCreateDTO) {
        ApiResponse<ImageDiagnostic> response = imageDiagnosticService.create(imageDiagnosticCreateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar un diagnóstico de imagen
     * @param id ID del diagnóstico a eliminar
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = imageDiagnosticService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
