package Backend.ms_clasificator.DTOs.MedicalImg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear una imagen médica vía multipart/form-data.
 *
 * CAMBIO vs original:
 * - Se elimina el campo `url` — ya no se recibe URL externa.
 * - El archivo real llega como MultipartFile en el @RequestParam del controller.
 * - Este DTO solo transporta los metadatos de contexto.
 *
 * Por qué separar el archivo del DTO:
 * Spring MVC no puede bindear un MultipartFile dentro de un @RequestBody JSON.
 * Con multipart/form-data, el archivo va en un @RequestParam aparte
 * y los campos del DTO como @ModelAttribute o también como @RequestParam.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImgCreateDTO {

    @NotNull(message = "El ID del área de evaluación no puede ser nulo")
    private Integer evaluationAreaId;

    // Opcional: puede crearse sin paciente y asignarse después
    private Integer patientId;

    /**
     * Carpeta lógica donde se almacenará en el storage.
     * Por defecto: "diagnostics". También puede ser "labels", "reports", etc.
     * Si no se envía, el service lo defaultea a "diagnostics".
     */
    private String folder;
}