package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticResponseDTO;
import Backend.ms_clasificator.DTOs.ImageDiagnostic.ImageDiagnosticSummaryDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import Backend.ms_clasificator.Services.ImageDiagnosticService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/image-diagnostics")
public class ImageDiagnosticController {

    @Autowired
    private ImageDiagnosticService imageDiagnosticService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<ImageDiagnosticSummaryDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<ImageDiagnosticSummaryDTO>> response = imageDiagnosticService.findAll(pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(imageDiagnosticService.count());
    }

    /**
     * Obtener un diagnóstico de imagen por ID
     * @param id ID del diagnóstico
     * @return Diagnóstico encontrado
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<ImageDiagnosticResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<ImageDiagnosticResponseDTO> response = imageDiagnosticService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("doctor/{doctorId}")
    public ResponseEntity<ApiResponse<PagedResponse<ImageDiagnosticSummaryDTO>>> findByDoctorId(
            @PathVariable Integer doctorId,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<ImageDiagnosticSummaryDTO>> response = imageDiagnosticService.findByDoctorId(doctorId, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("image/{medicalImageId}")
    public ResponseEntity<ApiResponse<PagedResponse<ImageDiagnosticSummaryDTO>>> findByMedicalImageId(
            @PathVariable Integer medicalImageId,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<ImageDiagnosticSummaryDTO>> response = imageDiagnosticService.findByMedicalImgId(medicalImageId, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    /**
     * Crear un nuevo diagnóstico de imagen
     * @param imageDiagnosticCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<ImageDiagnosticResponseDTO>> create(@Valid @RequestBody ImageDiagnosticCreateDTO imageDiagnosticCreateDTO) {
        ApiResponse<ImageDiagnosticResponseDTO> response = imageDiagnosticService.create(imageDiagnosticCreateDTO);

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
