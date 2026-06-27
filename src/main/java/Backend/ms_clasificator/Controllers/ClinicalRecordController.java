package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordCreateDTO;
import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordResponseDTO;
import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordSummaryDTO;
import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordUpdateDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import Backend.ms_clasificator.Services.ClinicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinical-records")
public class ClinicalRecordController {

	@Autowired
	private ClinicalRecordService clinicalRecordService;

	@GetMapping("")
	public ResponseEntity<ApiResponse<PagedResponse<ClinicalRecordSummaryDTO>>> findAll(
			@Valid @ModelAttribute PageRequestDTO pageRequest) {
		ApiResponse<PagedResponse<ClinicalRecordSummaryDTO>> response = this.clinicalRecordService.findAll(pageRequest);
		return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
	}

	@GetMapping("count")
	public ResponseEntity<ApiResponse<Long>> count() {
		return ResponseEntity.ok(clinicalRecordService.count());
	}

	@GetMapping("{id}")
	public ResponseEntity<ApiResponse<ClinicalRecordResponseDTO>> findById(@PathVariable Integer id) {
		ApiResponse<ClinicalRecordResponseDTO> response = this.clinicalRecordService.findById(id);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@GetMapping("patient/{patientId}")
	public ResponseEntity<ApiResponse<PagedResponse<ClinicalRecordSummaryDTO>>> findByPatientId(
			@PathVariable Integer patientId,
			@Valid @ModelAttribute PageRequestDTO pageRequest) {
		ApiResponse<PagedResponse<ClinicalRecordSummaryDTO>> response = this.clinicalRecordService.findByPatientId(patientId, pageRequest);
		return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
	}

	@PostMapping("")
	public ResponseEntity<ApiResponse<ClinicalRecordResponseDTO>> create(@Valid @org.springframework.web.bind.annotation.RequestBody ClinicalRecordCreateDTO dto) {
		ApiResponse<ClinicalRecordResponseDTO> response = this.clinicalRecordService.create(dto);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PutMapping("{id}")
	public ResponseEntity<ApiResponse<ClinicalRecordResponseDTO>> update(@PathVariable Integer id, @Valid @org.springframework.web.bind.annotation.RequestBody ClinicalRecordUpdateDTO dto) {
		ApiResponse<ClinicalRecordResponseDTO> response = this.clinicalRecordService.update(id, dto);
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
	public ResponseEntity<ApiResponse<ClinicalRecordResponseDTO>> changePatient(
			@PathVariable Integer clinicalRecordId,
			@PathVariable Integer newPatientId
	) {
		ApiResponse<ClinicalRecordResponseDTO> response = this.clinicalRecordService.changePatient(clinicalRecordId, newPatientId);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}
}
