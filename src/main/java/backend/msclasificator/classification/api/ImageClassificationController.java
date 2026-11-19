package backend.msclasificator.classification.api;

import backend.msclasificator.classification.api.dto.ImageClassificationCreateDTO;
import backend.msclasificator.classification.api.dto.ImageClassificationResponseDTO;
import backend.msclasificator.classification.api.dto.ImageClassificationSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.classification.application.ImageClassificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/image-diagnostics")
public class ImageClassificationController {

    @Autowired
    private ImageClassificationService imageDiagnosticService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<ImageClassificationSummaryDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<ImageClassificationSummaryDTO>> response = imageDiagnosticService.findAll(pageRequest);
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
    public ResponseEntity<ApiResponse<ImageClassificationResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<ImageClassificationResponseDTO> response = imageDiagnosticService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("doctor/{doctorId}")
    public ResponseEntity<ApiResponse<PagedResponse<ImageClassificationSummaryDTO>>> findByDoctorId(
            @PathVariable Integer doctorId,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<ImageClassificationSummaryDTO>> response = imageDiagnosticService.findByDoctorId(doctorId, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("image/{medicalImageId}")
    public ResponseEntity<ApiResponse<PagedResponse<ImageClassificationSummaryDTO>>> findByMedicalImageId(
            @PathVariable Integer medicalImageId,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<ImageClassificationSummaryDTO>> response = imageDiagnosticService.findByMedicalImgId(medicalImageId, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    /**
     * Crear un nuevo diagnóstico de imagen
     * @param imageDiagnosticCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<ImageClassificationResponseDTO>> create(@Valid @RequestBody ImageClassificationCreateDTO imageDiagnosticCreateDTO) {
        ApiResponse<ImageClassificationResponseDTO> response = imageDiagnosticService.create(imageDiagnosticCreateDTO);

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
