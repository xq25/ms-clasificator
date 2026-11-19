package backend.msclasificator.clinicalrecord.api;

import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordSummaryDTO;
import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordUpdateDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.clinicalrecord.application.ClinicalRecordService;
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
