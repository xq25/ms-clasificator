package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategoryCreateDTO;
import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategoryResponseDTO;
import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategorySummaryDTO;
import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategoryUpdateDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import Backend.ms_clasificator.Services.DatasetCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dataset-categories")
public class DatasetCategoryController {

    @Autowired
    private DatasetCategoryService datasetCategoryService;

    /**
     * Obtener todas las categorias
     * @return Lista de todos los estados
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<DatasetCategoryResponseDTO>>> findAll(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        ApiResponse<PagedResponse<DatasetCategoryResponseDTO>> response = this.datasetCategoryService.findAll(pageRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(this.datasetCategoryService.count());
    }

    /**
     * Obtener una categoria por ID
     * @param id ID del estado
     * @return Estado encontrado
     */
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<DatasetCategoryResponseDTO>> findById(@PathVariable Integer id) {
        ApiResponse<DatasetCategoryResponseDTO> response = this.datasetCategoryService.findById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener todos las categorias que pertenecen a un dataset
     * @param datasetId ID de la configuración
     * @return Lista de categorias del dataset
     */
    @GetMapping("dataset/{datasetId}")
    public ResponseEntity<ApiResponse<List<DatasetCategorySummaryDTO>>> findByDatasetId(@PathVariable Integer datasetId) {
        ApiResponse<java.util.List<DatasetCategorySummaryDTO>> response = this.datasetCategoryService.findByDatasetId(datasetId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }


    /**
     * Crear una nueva categoria de un dataset
     * @param datasetCategoryCreateDTO DTO con datos de entrada
     * @return ApiResponse con el resultado
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<DatasetCategoryResponseDTO>> create(@Valid @RequestBody DatasetCategoryCreateDTO datasetCategoryCreateDTO) {
        ApiResponse<DatasetCategoryResponseDTO> response = this.datasetCategoryService.create(datasetCategoryCreateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<DatasetCategoryResponseDTO>> update(@PathVariable Integer id, @Valid @RequestBody DatasetCategoryUpdateDTO datasetCategoryUpdateDTO) {
        ApiResponse<DatasetCategoryResponseDTO> response = this.datasetCategoryService.update(id, datasetCategoryUpdateDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }



    /**
     * Eliminar una categoria de un dataset
     * @param id ID de la categoria a eliminar
     * @return ApiResponse con el resultado
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        ApiResponse<Void> response = this.datasetCategoryService.delete(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
