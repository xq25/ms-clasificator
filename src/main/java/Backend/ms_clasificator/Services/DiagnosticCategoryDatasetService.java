package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetCreateDTO;
import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetResponseDTO;
import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetSummaryDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import org.springframework.data.domain.Page;
import Backend.ms_clasificator.Mappers.DiagnosticCategoryDatasetMappers.DiagnosticCategoryDatasetMapper;
import Backend.ms_clasificator.Models.DatasetCategory;
import Backend.ms_clasificator.Models.DiagnosticCategoryDataset;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Repositories.DatasetCategoryRepository;
import Backend.ms_clasificator.Repositories.DiagnosticCategoryDatasetRepository;
import Backend.ms_clasificator.Repositories.MedicalDiagnosticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiagnosticCategoryDatasetService {
    @Autowired
    private DiagnosticCategoryDatasetRepository repository;

    @Autowired
    private DiagnosticCategoryDatasetMapper mapper;

    @Autowired
    private DatasetCategoryRepository datasetCategoryRepository;

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<DiagnosticCategoryDatasetSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        try {
            Page<DiagnosticCategoryDatasetSummaryDTO> page = repository.findAll(pageRequest.toPageable())
                    .map(mapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<DiagnosticCategoryDatasetSummaryDTO>builder()
                            .content(page.getContent())
                            .page(page.getNumber())
                            .size(page.getSize())
                            .totalElements(page.getTotalElements())
                            .totalPages(page.getTotalPages())
                            .last(page.isLast())
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
        return ApiResponse.success(repository.countAll(), "Total de DiagnosticCategoryDatasets");
    }

    @Transactional(readOnly = true)
    public ApiResponse<DiagnosticCategoryDatasetResponseDTO> findById(Integer id) {
        try {

            DiagnosticCategoryDatasetResponseDTO entity = repository.findById(id)
                    .map(mapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("DiagnosticCategoryDataset no encontrado con ID: " + id));

            return ApiResponse.success(entity, "DiagnosticCategoryDataset obtenido exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<DiagnosticCategoryDatasetSummaryDTO>> findByDatasetCategoryId(Integer datasetCategoryId) {

        try {

            if (!datasetCategoryRepository.existsById(datasetCategoryId)) {
                return ApiResponse.error("DatasetCategory no encontrado con ID: " + datasetCategoryId);
            }

            List<DiagnosticCategoryDatasetSummaryDTO> data =
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
    public ApiResponse<DiagnosticCategoryDatasetResponseDTO> create(DiagnosticCategoryDatasetCreateDTO dto) {
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

            if (repository.existsByDatasetCategoryDatasetIdAndMedicalDiagnosticId(
                    datasetCategory.getDataset().getId(),
                    medicalDiagnostic.getId())) {

                return ApiResponse.error(
                        "El diagnóstico seleccionado ya pertenece a otra categoría del mismo dataset"
                );
            }

            DiagnosticCategoryDataset entity =
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

            DiagnosticCategoryDataset entity =
                    repository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "DiagnosticCategoryDataset no encontrado con ID: " + id));

            repository.delete(entity);

            return ApiResponse.success(
                    "DiagnosticCategoryDataset eliminado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Implementar según tu modelo de jerarquía de diagnósticos.
     */
    private boolean isValidSubDiagnostic(MedicalDiagnostic diagnostic, MedicalDiagnostic rootDiagnostic) {
        medicalDiagnosticRepository.existsByIdAndParentDiagnosticId(diagnostic.getId(), rootDiagnostic.getId());

        return true;
    }
}
