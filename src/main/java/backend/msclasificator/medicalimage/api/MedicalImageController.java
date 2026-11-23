package backend.msclasificator.medicalimage.api;

import backend.msclasificator.medicalimage.api.dto.MedicalImageCreateDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageResponseDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.medicalimage.application.MedicalImageService;
import backend.msclasificator.medicalimage.application.port.out.ImageFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Controller actualizado para manejo de imágenes médicas con storage desacoplado.
 * CAMBIO PRINCIPAL vs original:
 * - El endpoint POST ahora recibe multipart/form-data en lugar de JSON.
 * - El archivo va como @RequestPart("file") MultipartFile.
 * - Los metadatos del DTO van como @RequestPart("data") o como @RequestParam individuales.
 * Por qué multipart y no base64 en JSON:
 * - Base64 en JSON infla el payload ~33% → más lento y más memoria.
 * - Multipart es el estándar para file upload en HTTP.
 * - Spring MVC lo maneja nativamente con MultipartFile.
 * CONSUMES = multipart/form-data solo en el endpoint de upload.
 * Los demás endpoints siguen siendo JSON normal.
 */
@RestController
@RequestMapping("/api/medical-images")
@RequiredArgsConstructor
public class MedicalImageController {

    private final MedicalImageService medicalImageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MedicalImageResponseDTO>> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("medicalImageTypeId") Integer medicalImageTypeId,
            @RequestParam(value = "folder", defaultValue = "diagnostics") String folder,
            @RequestParam(value = "clinicalRecordId", required = false) Integer clinicalRecordId) {

        MedicalImageCreateDTO dto = MedicalImageCreateDTO.builder()
                .medicalImageTypeId(medicalImageTypeId)
                .folder(folder)
                .clinicalRecordId(clinicalRecordId)  // ← agregar
                .build();

        // MultipartFile no sale de la capa web: se convierte en ImageFile (ADR-012).
        // El controlador abre el stream y lo cierra al terminar la petición.
        ApiResponse<MedicalImageResponseDTO> response;
        try (InputStream content = file.getInputStream()) {
            ImageFile imageFile = new ImageFile(file.getOriginalFilename(), file.getContentType(), file.getSize(), content);
            response = medicalImageService.uploadImage(imageFile, dto);
        } catch (IOException ex) {
            response = ApiResponse.error("No se pudo leer el archivo de imagen: " + ex.getMessage());
        }

        return response.isSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.badRequest().body(response);
    }

    // LECTURA
    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<MedicalImageSummaryDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<MedicalImageSummaryDTO>> response = medicalImageService.findAll(pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(medicalImageService.count());
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<MedicalImageResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<MedicalImageResponseDTO> response = medicalImageService.findById(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("image-type/{medicalImageTypeId}")
    public ResponseEntity<ApiResponse<PagedResponse<MedicalImageResponseDTO>>> findByMedicalImageType(
            @PathVariable Integer medicalImageTypeId,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<MedicalImageResponseDTO>> response =
                medicalImageService.findByMedicalImageType(medicalImageTypeId, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("clinical-record/{clinicalRecordId}")
    public ResponseEntity<ApiResponse<PagedResponse<MedicalImageSummaryDTO>>> findByClinicalRecord(
            @PathVariable Integer clinicalRecordId,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<MedicalImageSummaryDTO>> response =
                medicalImageService.findByClinicalRecord(clinicalRecordId, pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("undianosed/doctor/{doctorId}/image-type/{medicalImageTypeId}")
    public ResponseEntity<ApiResponse<List<MedicalImageResponseDTO>>> findUndiagnosedByDoctorAndImageType(@PathVariable Integer doctorId, @PathVariable Integer medicalImageTypeId) {
        ApiResponse<List<MedicalImageResponseDTO>> response =
                medicalImageService.findUndiagnosedByDoctorAndMedicalImageType(doctorId, medicalImageTypeId);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    // DELETE
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = medicalImageService.delete(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

}