package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.Patient.PatientCreateDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientResponseDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientSummaryDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Services.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * Obtener todos los pacientes
     * @return Lista de todos los pacientes
     */
    @GetMapping("")
    public ResponseEntity<List<PatientSummaryDTO>> findAll() {
        List<PatientSummaryDTO> patients = patientService.findAll();
        return ResponseEntity.ok(patients);
    }

    /**
     * Obtener un paciente por ID
     * @param id ID del paciente
     * @return Paciente encontrado
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<PatientResponseDTO> response = patientService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("user-id/{userId}")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> findByUserId(@PathVariable String userId){
        ApiResponse<PatientResponseDTO> response = patientService.findByUserId(userId);

        if(response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener un paciente por documento
     * @param document Documento del paciente
     * @return Paciente encontrado
     */
    @GetMapping("document/{document}")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> findByDocument(@PathVariable String document) {
        ApiResponse<PatientResponseDTO> response = patientService.findByDocument(document);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
     }

    /**
     * Crear un nuevo paciente
     * @param patientCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<PatientSummaryDTO>> create(@Valid @RequestBody PatientCreateDTO patientCreateDTO) {
        ApiResponse<PatientSummaryDTO> response = patientService.create(patientCreateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar un paciente existente
     * @param id ID del paciente a actualizar
     * @param patientUpdateDTO DTO con datos a actualizar
     * @return ApiResponse con el resultado
     */
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<PatientSummaryDTO>> update(@PathVariable Integer id, @Valid @RequestBody PatientUpdateDTO patientUpdateDTO) {
        ApiResponse<PatientSummaryDTO> response = patientService.update(id, patientUpdateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar un paciente
     * @param id ID del paciente a eliminar
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = patientService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
