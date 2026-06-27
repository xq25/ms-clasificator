package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumCreateDTO;
import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumResponseDTO;
import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import Backend.ms_clasificator.Services.PrimitiveDatumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/primitive-datums")
public class PrimitiveDatumController {

	@Autowired
	private PrimitiveDatumService primitiveDatumService;

	@GetMapping("")
	public ResponseEntity<ApiResponse<PagedResponse<PrimitiveDatumResponseDTO>>> findAll(
			@Valid @ModelAttribute PageRequestDTO pageRequest) {
		ApiResponse<PagedResponse<PrimitiveDatumResponseDTO>> response = this.primitiveDatumService.findAll(pageRequest);
		return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
	}

	@GetMapping("count")
	public ResponseEntity<ApiResponse<Long>> count() {
		return ResponseEntity.ok(primitiveDatumService.count());
	}

	@GetMapping("{id}")
	public ResponseEntity<ApiResponse<PrimitiveDatumResponseDTO>> findById(@PathVariable Integer id) {
		ApiResponse<PrimitiveDatumResponseDTO> response = this.primitiveDatumService.findById(id);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PostMapping("")
	public ResponseEntity<ApiResponse<PrimitiveDatumResponseDTO>> create(@Valid @RequestBody PrimitiveDatumCreateDTO dto) {
		ApiResponse<PrimitiveDatumResponseDTO> response = this.primitiveDatumService.create(dto);
		if (response.isSuccess()) return ResponseEntity.status(HttpStatus.CREATED).body(response);
		return ResponseEntity.badRequest().body(response);
	}

	@PutMapping("{id}")
	public ResponseEntity<ApiResponse<PrimitiveDatumResponseDTO>> update(@PathVariable Integer id, @Valid @RequestBody PrimitiveDatumUpdateDTO dto) {
		ApiResponse<PrimitiveDatumResponseDTO> response = this.primitiveDatumService.update(id, dto);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
		ApiResponse<Void> response = this.primitiveDatumService.delete(id);
		if (response.isSuccess()) return ResponseEntity.ok(response);
		return ResponseEntity.badRequest().body(response);
	}
}
