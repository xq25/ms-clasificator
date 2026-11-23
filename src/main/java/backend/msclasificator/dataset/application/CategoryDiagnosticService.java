package backend.msclasificator.dataset.application;

import backend.msclasificator.dataset.api.dto.CategoryDiagnosticCreateDTO;
import backend.msclasificator.dataset.api.dto.CategoryDiagnosticResponseDTO;
import backend.msclasificator.dataset.api.dto.CategoryDiagnosticSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.dataset.api.mapper.CategoryDiagnosticMapper;
import backend.msclasificator.dataset.domain.DatasetCategory;
import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.dataset.application.port.out.DatasetCategoryRepository;
import backend.msclasificator.dataset.application.port.out.CategoryDiagnosticRepository;
import backend.msclasificator.diagnosiscatalog.application.port.out.MedicalDiagnosticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryDiagnosticService {
    @Autowired
    private CategoryDiagnosticRepository repository;

    @Autowired
    private CategoryDiagnosticMapper mapper;

    @Autowired
    private DatasetCategoryRepository datasetCategoryRepository;

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<CategoryDiagnosticSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        try {
            PageResult<CategoryDiagnosticSummaryDTO> page = repository.findAll(pageRequest.toPageQuery())
                    .map(mapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<CategoryDiagnosticSummaryDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
                            .build(),
                    "DiagnosticCategoryDatasets obtenidos exitosamente"
            );

        } catch (Exception ex) {
            return ApiResponse.error(
                    "Error al listar DiagnosticCategoryDatasets: "
                            + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(repository.count(), "Total de DiagnosticCategoryDatasets");
    }

    @Transactional(readOnly = true)
    public ApiResponse<CategoryDiagnosticResponseDTO> findById(Integer id) {
        try {

            CategoryDiagnosticResponseDTO entity = repository.findById(id)
                    .map(mapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("CategoryDiagnostic no encontrado con ID: " + id));

            return ApiResponse.success(entity, "CategoryDiagnostic obtenido exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<CategoryDiagnosticSummaryDTO>> findByDatasetCategoryId(Integer datasetCategoryId) {

        try {

            if (!datasetCategoryRepository.existsById(datasetCategoryId)) {
                return ApiResponse.error("DatasetCategory no encontrado con ID: " + datasetCategoryId);
            }

            List<CategoryDiagnosticSummaryDTO> data =
                    repository.findByDatasetCategoryId(datasetCategoryId)
                            .stream()
                            .map(mapper::toSummaryDTO)
                            .toList();

            return ApiResponse.success(
                    data,
                    "Diagnósticos obtenidos exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error(
                    "Error al buscar diagnósticos: "
                            + ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<CategoryDiagnosticResponseDTO> create(CategoryDiagnosticCreateDTO dto) {
        try {
            if (dto == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            DatasetCategory datasetCategory =
                    datasetCategoryRepository.findById(dto.getDatasetCategoryId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "DatasetCategory no encontrado con ID: "
                                                    + dto.getDatasetCategoryId()
                                    ));

            MedicalDiagnostic medicalDiagnostic =
                    medicalDiagnosticRepository.findById(dto.getMedicalDiagnosticId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "MedicalDiagnostic no encontrado con ID: "
                                                    + dto.getMedicalDiagnosticId()
                                    ));

            MedicalDiagnostic rootDiagnostic =
                    datasetCategory.getDataset().getMedicalDiagnostic();

            if (!isValidSubDiagnostic(
                    medicalDiagnostic,
                    rootDiagnostic)) {

                return ApiResponse.error(
                        "El diagnóstico seleccionado no pertenece al árbol de clasificación definido por el Dataset"
                );
            }

            if (repository.existsByDatasetIdAndMedicalDiagnosticId(
                    datasetCategory.getDataset().getId(),
                    medicalDiagnostic.getId())) {

                return ApiResponse.error(
                        "El diagnóstico seleccionado ya pertenece a otra categoría del mismo dataset"
                );
            }

            CategoryDiagnostic entity =
                    mapper.toEntity(dto);

            entity.setDatasetCategory(datasetCategory);
            entity.setMedicalDiagnostic(medicalDiagnostic);

            return ApiResponse.success(mapper.toResponseDTO(repository.save(entity)), "Diagnóstico agregado exitosamente a la categoría");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }

    }

    @Transactional
    public ApiResponse<Void> delete(Integer id) {

        try {

            CategoryDiagnostic entity =
                    repository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "CategoryDiagnostic no encontrado con ID: " + id));

            // Soft delete (ADR-011): queda en el historial de la definición de la categoría
            entity.retire();
            repository.save(entity);

            return ApiResponse.success(
                    "CategoryDiagnostic eliminado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    // Válido si el diagnóstico desciende (a cualquier profundidad) de la raíz del dataset; la raíz misma no es válida.
    // El set de visitados evita un bucle infinito si hubiera un ciclo en parent_diagnostic_id.
    private boolean isValidSubDiagnostic(MedicalDiagnostic diagnostic, MedicalDiagnostic rootDiagnostic) {
        Set<Integer> visitados = new HashSet<>();
        MedicalDiagnostic actual = diagnostic.getParentDiagnostic();
        while (actual != null && visitados.add(actual.getId())) {
            if (actual.getId().equals(rootDiagnostic.getId())) {
                return true;
            }
            actual = actual.getParentDiagnostic();
        }
        return false;
    }
}
