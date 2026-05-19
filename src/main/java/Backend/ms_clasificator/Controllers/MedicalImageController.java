package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgResponseDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Services.MedicalImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller actualizado para manejo de imágenes médicas con storage desacoplado.
 *
 * CAMBIO PRINCIPAL vs original:
 * - El endpoint POST ahora recibe multipart/form-data en lugar de JSON.
 * - El archivo va como @RequestPart("file") MultipartFile.
 * - Los metadatos del DTO van como @RequestPart("data") o como @RequestParam individuales.
 *
 * Por qué multipart y no base64 en JSON:
 * - Base64 en JSON infla el payload ~33% → más lento y más memoria.
 * - Multipart es el estándar para file upload en HTTP.
 * - Spring MVC lo maneja nativamente con MultipartFile.
 *
 * CONSUMES = multipart/form-data solo en el endpoint de upload.
 * Los demás endpoints siguen siendo JSON normal.
 */
@RestController
@RequestMapping("/api/medical-images")
@RequiredArgsConstructor
public class MedicalImageController {

    private final MedicalImageService medicalImageService;

    // =====================================================================
    // UPLOAD — multipart/form-data
    // =====================================================================

    /**
     * POST /medical-images/upload
     *
     * Ejemplo con curl:
     * curl -X POST http://localhost:8081/medical-images/upload \
     *   -F "file=@imagen.jpg;type=image/jpeg" \
     *   -F "evaluationAreaId=1" \
     *   -F "folder=diagnostics"
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MedicalImgResponseDTO>> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("evaluationAreaId") Integer evaluationAreaId,
            @RequestParam(value = "patientId", required = false) Integer patientId,
            @RequestParam(value = "folder", defaultValue = "diagnostics") String folder) {

        MedicalImgCreateDTO dto = MedicalImgCreateDTO.builder()
                .evaluationAreaId(evaluationAreaId)
                .patientId(patientId)
                .folder(folder)
                .build();

        ApiResponse<MedicalImgResponseDTO> response = medicalImageService.uploadImage(file, dto);

        return response.isSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.badRequest().body(response);
    }

    // =====================================================================
    // LECTURA
    // =====================================================================

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<MedicalImgResponseDTO>>> findAll() {
        return ResponseEntity.ok(medicalImageService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<MedicalImgResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<MedicalImgResponseDTO> response = medicalImageService.findById(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("area/{evaluationAreaId}")
    public ResponseEntity<ApiResponse<List<MedicalImgResponseDTO>>> findByEvaluationAreaId(
            @PathVariable Integer evaluationAreaId) {
        ApiResponse<List<MedicalImgResponseDTO>> response =
                medicalImageService.findByEvaluationAreaId(evaluationAreaId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    // =====================================================================
    // DELETE
    // =====================================================================

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = medicalImageService.delete(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    // =====================================================================
    // RELACIONES
    // =====================================================================

    @PutMapping("{medicalImgId}/assign-patient/{patientId}")
    public ResponseEntity<ApiResponse<MedicalImgResponseDTO>> assignPatient(
            @PathVariable Integer medicalImgId, @PathVariable Integer patientId) {
        ApiResponse<MedicalImgResponseDTO> response = medicalImageService.assignPatient(medicalImgId, patientId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("{medicalImgId}/remove-patient")
    public ResponseEntity<ApiResponse<MedicalImgResponseDTO>> removePatient(@PathVariable Integer medicalImgId) {
        ApiResponse<MedicalImgResponseDTO> response = medicalImageService.removePatient(medicalImgId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("{medicalImgId}/change-evaluation-area/{evaluationAreaId}")
    public ResponseEntity<ApiResponse<MedicalImgResponseDTO>> changeEvaluationArea(
            @PathVariable Integer medicalImgId, @PathVariable Integer evaluationAreaId) {
        ApiResponse<MedicalImgResponseDTO> response =
                medicalImageService.changeEvaluationArea(medicalImgId, evaluationAreaId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}