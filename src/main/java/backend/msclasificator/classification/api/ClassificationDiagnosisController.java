package backend.msclasificator.classification.api;


import backend.msclasificator.classification.api.dto.ClassificationDiagnosisCreateDTO;
import backend.msclasificator.classification.api.dto.ClassificationDiagnosisResponseDTO;
import backend.msclasificator.classification.api.dto.ClassificationDiagnosisSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.classification.application.ClassificationDiagnosisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/image-doctor-diagnostics")
public class ClassificationDiagnosisController {

    @Autowired
    private ClassificationDiagnosisService imageDoctorDiagnosticsService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<ClassificationDiagnosisSummaryDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<ClassificationDiagnosisSummaryDTO>> response =
                imageDoctorDiagnosticsService.findAll(pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(imageDoctorDiagnosticsService.count());
    }

    /**
     * Obtener una asociación por ID
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<ClassificationDiagnosisResponseDTO>> findById(@PathVariable Integer id) {

        ApiResponse<ClassificationDiagnosisResponseDTO> response = imageDoctorDiagnosticsService.findById(id);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("image-diagnostic/{imageDiagnosticId}")
    public ResponseEntity<ApiResponse<PagedResponse<ClassificationDiagnosisSummaryDTO>>> findByImageDiagnosticId(
            @PathVariable Integer imageDiagnosticId,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<ClassificationDiagnosisSummaryDTO>> response =
                imageDoctorDiagnosticsService.findByImageDiagnosticId(imageDiagnosticId, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    /**
     * Crear una nueva asociación imagen-diagnóstico
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<ClassificationDiagnosisResponseDTO>> create(@Valid @RequestBody ClassificationDiagnosisCreateDTO dto) {

        ApiResponse<ClassificationDiagnosisResponseDTO> response = imageDoctorDiagnosticsService.create(dto);

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
