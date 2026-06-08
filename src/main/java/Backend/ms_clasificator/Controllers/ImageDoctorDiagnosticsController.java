package Backend.ms_clasificator.Controllers;


import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsResponseDTO;
import Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics.ImageDoctorDiagnosticsSummaryDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Services.ImageDoctorDiagnosticsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/image-doctor-diagnostics")
public class ImageDoctorDiagnosticsController {

    @Autowired
    private ImageDoctorDiagnosticsService imageDoctorDiagnosticsService;

    /**
     * Obtener todas las asociaciones imagen-diagnóstico
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ImageDoctorDiagnosticsSummaryDTO>>> findAll() {

        ApiResponse<List<ImageDoctorDiagnosticsSummaryDTO>> response = imageDoctorDiagnosticsService.findAll();

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * Obtener una asociación por ID
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<ImageDoctorDiagnosticsResponseDTO>> findById(@PathVariable Integer id) {

        ApiResponse<ImageDoctorDiagnosticsResponseDTO> response = imageDoctorDiagnosticsService.findById(id);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * Obtener todos los diagnósticos asociados a una imagen
     */
    @GetMapping("image-diagnostic/{imageDiagnosticId}")
    public ResponseEntity<ApiResponse<List<ImageDoctorDiagnosticsSummaryDTO>>> findByImageDiagnosticId(@PathVariable Integer imageDiagnosticId) {

        ApiResponse<List<ImageDoctorDiagnosticsSummaryDTO>> response = imageDoctorDiagnosticsService.findByImageDiagnosticId(imageDiagnosticId);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * Crear una nueva asociación imagen-diagnóstico
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<ImageDoctorDiagnosticsResponseDTO>> create(@Valid @RequestBody ImageDoctorDiagnosticsCreateDTO dto) {

        ApiResponse<ImageDoctorDiagnosticsResponseDTO> response = imageDoctorDiagnosticsService.create(dto);

        return response.isSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * Eliminar una asociación imagen-diagnóstico
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {

        ApiResponse<Void> response = imageDoctorDiagnosticsService.delete(id);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}
