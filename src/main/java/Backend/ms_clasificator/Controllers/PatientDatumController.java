package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.PatientDatum.PatientDatumCreateDTO;
import Backend.ms_clasificator.DTOs.PatientDatum.PatientDatumResponseDTO;
import Backend.ms_clasificator.DTOs.PatientDatum.PatientDatumSummaryDTO;
import Backend.ms_clasificator.DTOs.PatientDatum.PatientDatumUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import Backend.ms_clasificator.Services.PatientDatumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patient-datums")
public class PatientDatumController {

	@Autowired
	private PatientDatumService patientDatumService;

	@GetMapping("")
	public ResponseEntity<ApiResponse<PagedResponse<PatientDatumSummaryDTO>>> findAll(
			@Valid @ModelAttribute PageRequestDTO pageRequest) {
		ApiResponse<PagedResponse<PatientDatumSummaryDTO>> response = this.patientDatumService.findAll(pageRequest);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@GetMapping("count")
	public ResponseEntity<ApiResponse<Long>> count() {
		return ResponseEntity.ok(this.patientDatumService.count());
	}

	@GetMapping("{id}")
	public ResponseEntity<ApiResponse<PatientDatumResponseDTO>> findById(@PathVariable Integer id) {
		ApiResponse<PatientDatumResponseDTO> response = this.patientDatumService.findById(id);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@GetMapping("clinical-record/{clinicalRecordId}")
	public ResponseEntity<ApiResponse<List<PatientDatumResponseDTO>>> findByClinicalRecordId(@PathVariable Integer clinicalRecordId) {
		ApiResponse<List<PatientDatumResponseDTO>> response = this.patientDatumService.findByClinicalRecordId(clinicalRecordId);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@GetMapping("primitive-datum/{primitiveDatumId}")
	public ResponseEntity<ApiResponse<List<PatientDatumSummaryDTO>>> findByPrimitiveDatumId(@PathVariable Integer primitiveDatumId) {
		ApiResponse<List<PatientDatumSummaryDTO>> response = this.patientDatumService.findByPrimitiveDatumId(primitiveDatumId);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PostMapping("")
	public ResponseEntity<ApiResponse<PatientDatumResponseDTO>> create(@RequestBody PatientDatumCreateDTO dto) {
		ApiResponse<PatientDatumResponseDTO> response = this.patientDatumService.create(dto);
		if (response.isSuccess()) return ResponseEntity.status(HttpStatus.CREATED).body(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PutMapping("{id}")
	public ResponseEntity<ApiResponse<PatientDatumResponseDTO>> update(@PathVariable Integer id, @RequestBody PatientDatumUpdateDTO dto) {
		ApiResponse<PatientDatumResponseDTO> response = this.patientDatumService.update(id, dto);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
		ApiResponse<Void> response = this.patientDatumService.delete(id);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}
}
