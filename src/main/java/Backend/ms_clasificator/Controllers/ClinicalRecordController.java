package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordCreateDTO;
import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordResponseDTO;
import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Models.ClinicalRecord;
import Backend.ms_clasificator.Services.ClinicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clinical-records")
public class ClinicalRecordController {

	@Autowired
	private ClinicalRecordService clinicalRecordService;

	@GetMapping("")
	public ResponseEntity<ApiResponse<List<ClinicalRecordResponseDTO>>> findAll() {
		ApiResponse<List<ClinicalRecordResponseDTO>> response = this.clinicalRecordService.findAll();
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@GetMapping("{id}")
	public ResponseEntity<ApiResponse<ClinicalRecordResponseDTO>> findById(@PathVariable Integer id) {
		ApiResponse<ClinicalRecordResponseDTO> response = this.clinicalRecordService.findById(id);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@GetMapping("patient/{patientId}")
	public ResponseEntity<ApiResponse<List<ClinicalRecordResponseDTO>>> findByPatientId(@PathVariable Integer patientId) {
		ApiResponse<List<ClinicalRecordResponseDTO>> response = this.clinicalRecordService.findByPatientId(patientId);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PostMapping("")
	public ResponseEntity<ApiResponse<ClinicalRecord>> create(@Valid @org.springframework.web.bind.annotation.RequestBody ClinicalRecordCreateDTO dto) {
		ApiResponse<ClinicalRecord> response = this.clinicalRecordService.create(dto);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PutMapping("{id}")
	public ResponseEntity<ApiResponse<ClinicalRecord>> update(@PathVariable Integer id, @Valid @org.springframework.web.bind.annotation.RequestBody ClinicalRecordUpdateDTO dto) {
		ApiResponse<ClinicalRecord> response = this.clinicalRecordService.update(id, dto);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
		ApiResponse<Void> response = this.clinicalRecordService.delete(id);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PutMapping("{clinicalRecordId}/patient/{newPatientId}")
	public ResponseEntity<ApiResponse<ClinicalRecord>> changePatient(
			@PathVariable Integer clinicalRecordId,
			@PathVariable Integer newPatientId
	) {
		ApiResponse<ClinicalRecord> response = this.clinicalRecordService.changePatient(clinicalRecordId, newPatientId);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}
}
