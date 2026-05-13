package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Services.MedicalImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-images")
public class MedicalImageController {

    @Autowired
    private MedicalImageService medicalImageService;

    /**
     * Obtener todas las imágenes médicas
     * @return Lista de todas las imágenes
     */
    @GetMapping("")
    public ResponseEntity<List<MedicalImg>> findAll() {
        List<MedicalImg> images = medicalImageService.findAll();
        return ResponseEntity.ok(images);
    }

    /**
     * Obtener una imagen médica por ID
     * @param id ID de la imagen
     * @return Imagen encontrada
     */
    @GetMapping("{id}")
    public ResponseEntity<MedicalImg> findById(@PathVariable Integer id) {
        MedicalImg image = medicalImageService.findById(id);
        if (image != null) {
            return ResponseEntity.ok(image);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener todas las imágenes médicas de un área de evaluación
     * @param evaluationAreaId ID del área de evaluación
     * @return ApiResponse con la lista de imágenes
     */
    @GetMapping("area/{evaluationAreaId}")
    public ResponseEntity<ApiResponse<List<MedicalImg>>> findByEvaluationAreaId(@PathVariable Integer evaluationAreaId) {
        ApiResponse<List<MedicalImg>> response = medicalImageService.findByEvaluationAreaId(evaluationAreaId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Crear una nueva imagen médica
     * @param medicalImgCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<MedicalImg>> create(@Valid @RequestBody MedicalImgCreateDTO medicalImgCreateDTO) {
        ApiResponse<MedicalImg> response = medicalImageService.create(medicalImgCreateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar una imagen médica existente
     * @param id ID de la imagen a actualizar
     * @param medicalImgUpdateDTO DTO con datos a actualizar
     * @return ApiResponse con el resultado
     */
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<MedicalImg>> update(@PathVariable Integer id, @Valid @RequestBody MedicalImgUpdateDTO medicalImgUpdateDTO) {
        ApiResponse<MedicalImg> response = medicalImageService.update(id, medicalImgUpdateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar una imagen médica
     * @param id ID de la imagen a eliminar
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = medicalImageService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ===== Relaciones con otras entidades =====

    /**
     * Asignar un paciente a una imagen médica
     * @param medicalImgId ID de la imagen
     * @param patientId ID del paciente
     * @return ApiResponse con el resultado
     */
    @PostMapping("{medicalImgId}/assign-patient/{patientId}")
    public ResponseEntity<ApiResponse<MedicalImg>> assignPatient(@PathVariable Integer medicalImgId, @PathVariable Integer patientId) {
        ApiResponse<MedicalImg> response = medicalImageService.assignPatient(medicalImgId, patientId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Remover un paciente de una imagen médica
     * @param medicalImgId ID de la imagen
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{medicalImgId}/remove-patient")
    public ResponseEntity<ApiResponse<MedicalImg>> removePatient(@PathVariable Integer medicalImgId) {
        ApiResponse<MedicalImg> response = medicalImageService.removePatient(medicalImgId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Cambiar el área de evaluación de una imagen médica
     * @param medicalImgId ID de la imagen
     * @param evaluationAreaId ID del área de evaluación
     * @return ApiResponse con el resultado
     */
    @PostMapping("{medicalImgId}/change-evaluation-area/{evaluationAreaId}")
    public ResponseEntity<ApiResponse<MedicalImg>> changeEvaluationArea(@PathVariable Integer medicalImgId, @PathVariable Integer evaluationAreaId) {
        ApiResponse<MedicalImg> response = medicalImageService.changeEvaluationArea(medicalImgId, evaluationAreaId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
