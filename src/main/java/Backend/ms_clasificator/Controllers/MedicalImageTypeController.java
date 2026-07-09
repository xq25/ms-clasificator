package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeSummaryDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeUpdateDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeResponseDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import Backend.ms_clasificator.Services.MedicalImageTypeService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-image-types")
public class MedicalImageTypeController {

	@Autowired
	private MedicalImageTypeService medicalImageTypeService;

	@GetMapping("")
	public ResponseEntity<ApiResponse<PagedResponse<MedicalImageTypeSummaryDTO>>> find(
			@Valid @ModelAttribute PageRequestDTO pageRequest){
		ApiResponse<PagedResponse<MedicalImageTypeSummaryDTO>> response = this.medicalImageTypeService.findAll(pageRequest);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@GetMapping("search/by-name")
	public ResponseEntity<ApiResponse<PagedResponse<MedicalImageTypeSummaryDTO>>> searchByName(
			@RequestParam String query,
			@Valid @ModelAttribute PageRequestDTO pageRequest) {
		ApiResponse<PagedResponse<MedicalImageTypeSummaryDTO>> response = medicalImageTypeService.searchByName(query, pageRequest);
		return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
	}

	@GetMapping("count")
	public ResponseEntity<ApiResponse<Long>> count() {
		return ResponseEntity.ok(this.medicalImageTypeService.count());
	}

	@GetMapping("{id}")
	public ResponseEntity<ApiResponse<MedicalImageTypeResponseDTO>> findById(@PathVariable Integer id){
		ApiResponse<MedicalImageTypeResponseDTO> response = this.medicalImageTypeService.findById(id);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@GetMapping("area/{evaluationAreaId}")
	public ResponseEntity<ApiResponse<List<MedicalImageTypeSummaryDTO>>> findByEvaluationAreaId(@PathVariable Integer evaluationAreaId){
		ApiResponse<List<MedicalImageTypeSummaryDTO>> response = this.medicalImageTypeService.findByEvaluationAreaId(evaluationAreaId);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PostMapping("")
	public ResponseEntity<ApiResponse<MedicalImageTypeResponseDTO>> create(@Valid @RequestBody MedicalImageTypeCreateDTO dto) {
		ApiResponse<MedicalImageTypeResponseDTO> response = this.medicalImageTypeService.create(dto);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PutMapping("{id}")
	public ResponseEntity<ApiResponse<MedicalImageTypeResponseDTO>> update(@PathVariable Integer id, @Valid @RequestBody MedicalImageTypeUpdateDTO dto) {
		ApiResponse<MedicalImageTypeResponseDTO> response = this.medicalImageTypeService.update(id, dto);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id){
		ApiResponse<Void> response = this.medicalImageTypeService.delete(id);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PostMapping("{id}/evaluation-area/{evaluationAreaId}")
	public ResponseEntity<ApiResponse<MedicalImageTypeResponseDTO>> assignEvaluationArea(@PathVariable("id") Integer id, @PathVariable Integer evaluationAreaId) {
		ApiResponse<MedicalImageTypeResponseDTO> response = this.medicalImageTypeService.assignEvaluationArea(id, evaluationAreaId);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@DeleteMapping("{id}/evaluation-area")
	public ResponseEntity<ApiResponse<MedicalImageTypeResponseDTO>> removeEvaluationArea(@PathVariable("id") Integer id) {
		ApiResponse<MedicalImageTypeResponseDTO> response = this.medicalImageTypeService.removeEvaluationArea(id);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}
}
