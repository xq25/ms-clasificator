package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetCreateDTO;
import Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset.DiagnosticCategoryDatasetResponseDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.DiagnosticCategoryDatasetMappers.DiagnosticCategoryDatasetMapper;
import Backend.ms_clasificator.Models.Dataset;
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
    public ApiResponse<List<DiagnosticCategoryDatasetResponseDTO>> findAll() {
        try {
            List<DiagnosticCategoryDatasetResponseDTO> data = repository.findAll()
                    .stream()
                    .map(mapper::toResponseDTO)
                    .toList();

            return ApiResponse.success(data,
                    "DiagnosticCategoryDatasets obtenidos exitosamente");

        } catch (Exception ex) {
            return ApiResponse.error(
                    "Error al listar DiagnosticCategoryDatasets: "
                            + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<DiagnosticCategoryDatasetResponseDTO> findById(Integer id) {
        try {

            DiagnosticCategoryDataset entity = repository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "DiagnosticCategoryDataset no encontrado con ID: " + id));

            return ApiResponse.success(
                    mapper.toResponseDTO(entity),
                    "DiagnosticCategoryDataset encontrado");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error(
                    "Error al buscar DiagnosticCategoryDataset: "
                            + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<DiagnosticCategoryDatasetResponseDTO>> findByDatasetCategoryId(Integer datasetCategoryId) {

        try {

            DatasetCategory datasetCategory =
                    datasetCategoryRepository.findById(datasetCategoryId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "DatasetCategory no encontrado con ID: "
                                            + datasetCategoryId));

            List<DiagnosticCategoryDatasetResponseDTO> data =
                    repository.findByDatasetCategoryId(datasetCategory.getId())
                            .stream()
                            .map(mapper::toResponseDTO)
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
                    datasetCategoryRepository.findById(
                                    dto.getDatasetCategoryId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "DatasetCategory no encontrado con ID: "
                                            + dto.getDatasetCategoryId()));

            MedicalDiagnostic medicalDiagnostic =
                    medicalDiagnosticRepository.findById(
                                    dto.getMedicalDiagnosticId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "MedicalDiagnostic no encontrado con ID: "
                                            + dto.getMedicalDiagnosticId()));

            Dataset dataset = datasetCategory.getDataset();

            MedicalDiagnostic rootDiagnostic =
                    dataset.getMedicalDiagnostic();

            // Validar que el diagnóstico seleccionado sea parte del árbol de clasificación definido por el Dataset (un subdiagnostico del diagnostico a clasificar)
            if (!isValidSubDiagnostic(medicalDiagnostic, rootDiagnostic)) {
                return ApiResponse.error("El diagnóstico seleccionado no pertenece al árbol de clasificación definido por el Dataset");
            }

            // Validar que el diagnostico no pertenezca ya a otra categoría del mismo dataset
            boolean exists = repository.existsByDatasetCategory_DatasetIdAndMedicalDiagnosticId(dataset.getId(), medicalDiagnostic.getId());
            if (exists) {
                return ApiResponse.error("El diagnóstico seleccionado ya pertenece a otra categoría del mismo dataset");
            }

            DiagnosticCategoryDataset entity = mapper.toEntity(dto);

            entity.setDatasetCategory(datasetCategory);
            entity.setMedicalDiagnostic(medicalDiagnostic);

            DiagnosticCategoryDataset saved = repository.save(entity);

            return ApiResponse.success(mapper.toResponseDTO(saved), "Diagnóstico agregado exitosamente a la categoria");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error(
                    "Error al crear DiagnosticCategoryDataset: "
                            + ex.getMessage());
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
        } catch (Exception ex) {
            return ApiResponse.error(
                    "Error al eliminar DiagnosticCategoryDataset: "
                            + ex.getMessage());
        }
    }

    /**
     * Implementar según tu modelo de jerarquía de diagnósticos.
     */
    private boolean isValidSubDiagnostic(MedicalDiagnostic diagnostic, MedicalDiagnostic rootDiagnostic) {
        medicalDiagnosticRepository.existsByIdAndParentDiagnostic_Id(diagnostic.getId(), rootDiagnostic.getId());

        return true;
    }
}
