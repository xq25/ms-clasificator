package backend.msclasificator.diagnosiscatalog.application;

import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticCreateDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticResponseDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticUpdateDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.diagnosiscatalog.api.mapper.MedicalDiagnosticMapper;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.dataset.application.port.out.CategoryDiagnosticRepository;
import backend.msclasificator.classification.infrastructure.persistence.ClassificationDiagnosisRepository;
import backend.msclasificator.diagnosiscatalog.application.port.out.MedicalDiagnosticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicalDiagnosticService {

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Autowired
    private ClassificationDiagnosisRepository imageDoctorDiagnosticsRepository;

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Autowired
    private CategoryDiagnosticRepository diagnosticCategoryDatasetRepository;



    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalDiagnosticResponseDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<MedicalDiagnosticResponseDTO> page = medicalDiagnosticRepository.findAll(pageRequest.toPageQuery())
                .map(medicalDiagnosticMapper::toResponseDTO);

        return ApiResponse.success(
                PagedResponse.<MedicalDiagnosticResponseDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Diagnósticos médicos encontrados exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalDiagnosticSummaryDTO>> searchByName(String query, PageRequestDTO pageRequest) {
        try {
            PageResult<MedicalDiagnosticSummaryDTO> page = medicalDiagnosticRepository
                    .searchByName(query, pageRequest.toPageQuery())
                    .map(medicalDiagnosticMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<MedicalDiagnosticSummaryDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
                            .build(),
                    "Resultados de búsqueda por nombre"
            );
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar diagnósticos por nombre: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalDiagnosticSummaryDTO>> searchByCode(String query, PageRequestDTO pageRequest) {
        try {
            PageResult<MedicalDiagnosticSummaryDTO> page = medicalDiagnosticRepository
                    .searchByCode(query, pageRequest.toPageQuery())
                    .map(medicalDiagnosticMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<MedicalDiagnosticSummaryDTO>builder()
                            .content(page.content())
                            .page(page.page())
                            .size(page.size())
                            .totalElements(page.totalElements())
                            .totalPages(page.totalPages())
                            .last(page.last())
                            .build(),
                    "Resultados de búsqueda por código"
            );
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar diagnósticos por código: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(medicalDiagnosticRepository.count(), "Total de diagnósticos médicos");
    }

    @Transactional(readOnly = true)
    public ApiResponse<MedicalDiagnosticResponseDTO> findById(Integer id) {
        try {
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + id));
            return ApiResponse.success(medicalDiagnosticMapper.toResponseDTO(medicalDiagnostic), "Diagnóstico médico encontrado");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar diagnóstico por ID: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<MedicalDiagnosticSummaryDTO>> findByParentDiagnosticId(Integer parentDiagnosticId) {
        try {
            if(!this.medicalDiagnosticRepository.existsById(parentDiagnosticId)){
                return ApiResponse.error("No se encontró un diagnóstico padre con ID: " + parentDiagnosticId);
            }

            List<MedicalDiagnosticSummaryDTO> diagnostics = medicalDiagnosticRepository
                    .findByParentDiagnosticId(parentDiagnosticId)
                    .stream()
                    .map(medicalDiagnosticMapper::toSummaryDTO)
                    .toList();
            return ApiResponse.success(diagnostics, "Diagnósticos médicos encontrados para el diagnóstico padre ID: " + parentDiagnosticId);
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar diagnósticos por ID de diagnóstico padre: " + ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<MedicalDiagnosticResponseDTO> create(MedicalDiagnosticCreateDTO medicalDiagnosticCreateDTO) {
        try {
            if (medicalDiagnosticCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validamos que el codigo sea unico
            if (medicalDiagnosticRepository.findByDiagnosticCode(medicalDiagnosticCreateDTO.getDiagnosticCode()).isPresent()) {
                return ApiResponse.error("Ya existe un diagnóstico con el código: " + medicalDiagnosticCreateDTO.getDiagnosticCode());
            }

            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticMapper.toEntity(medicalDiagnosticCreateDTO);
            if (medicalDiagnosticCreateDTO.getParentDiagnosticId() != null) {
                MedicalDiagnostic parentDiagnostic = medicalDiagnosticRepository
                        .findById(medicalDiagnosticCreateDTO.getParentDiagnosticId())
                        .orElseThrow(() -> new IllegalArgumentException("Diagnóstico padre no encontrado con ID: "
                                + medicalDiagnosticCreateDTO.getParentDiagnosticId()));
                // Si encuentra el diagnostico padre, se lo asignamos.
                medicalDiagnostic.setParentDiagnostic(parentDiagnostic);
            }

            MedicalDiagnostic saved = medicalDiagnosticRepository.save(medicalDiagnostic);
            return ApiResponse.success(medicalDiagnosticMapper.toResponseDTO(saved), "Diagnóstico médico creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<MedicalDiagnosticResponseDTO> update(Integer id, MedicalDiagnosticUpdateDTO medicalDiagnosticUpdateDTO) {
        try {
            if (medicalDiagnosticUpdateDTO == null) {
                throw new IllegalArgumentException("El DTO no puede ser nulo");
            }

            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + id));

            // Validamos el codigo de diagnostico
            MedicalDiagnostic existingByCode = medicalDiagnosticRepository
                    .findByDiagnosticCode(medicalDiagnosticUpdateDTO.getDiagnosticCode()).orElse(null);
            if (existingByCode != null && !existingByCode.getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe un diagnóstico con el código: "
                        + medicalDiagnosticUpdateDTO.getDiagnosticCode());
            }

            medicalDiagnostic.setDiagnosticCode(medicalDiagnosticUpdateDTO.getDiagnosticCode());
            medicalDiagnostic.setDiagnosticName(medicalDiagnosticUpdateDTO.getDiagnosticName());

            MedicalDiagnostic updated = medicalDiagnosticRepository.save(medicalDiagnostic);
            return ApiResponse.success(medicalDiagnosticMapper.toResponseDTO(updated), "Diagnóstico médico actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + id));

            // Validacion de subdiagnosticos
            if (medicalDiagnosticRepository.existsByParentDiagnosticId(id)) {
                return ApiResponse.error("El diagnóstico médico no se puede eliminar porque tiene sub-diagnósticos asociados.");
            }

            // Validamos que no tenga diagnosticos de imagenes asociados a el
            if (imageDoctorDiagnosticsRepository.existsByMedicalDiagnosticId(id)) {
                return ApiResponse.error("No se puede eliminar el diagnóstico ya que está asociado a clasificaciones de imágenes.");
            }

            medicalDiagnosticRepository.delete(medicalDiagnostic);
            return ApiResponse.success("Diagnóstico médico eliminado exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violación a la integridad de la base de datos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<MedicalDiagnosticResponseDTO> addSubDiagnostic(Integer parentDiagnosticId, Integer subDiagnosticId) {
        try {
            if (parentDiagnosticId == null || subDiagnosticId == null) {
                return ApiResponse.error("Los IDs del diagnóstico padre y del sub-diagnóstico no pueden ser nulos");
            }

            if (parentDiagnosticId.equals(subDiagnosticId)) {
                return ApiResponse.error("Un diagnóstico no puede ser sub-diagnóstico de sí mismo");
            }

            MedicalDiagnostic parentDiagnostic = medicalDiagnosticRepository.findById(parentDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico padre no encontrado con ID: " + parentDiagnosticId));

            MedicalDiagnostic subDiagnostic = medicalDiagnosticRepository.findById(subDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException("Sub-diagnóstico no encontrado con ID: " + subDiagnosticId));

            if (subDiagnostic.getParentDiagnostic() != null
                    && !subDiagnostic.getParentDiagnostic().getId().equals(parentDiagnosticId)) {
                throw new IllegalArgumentException("Este sub-diagnóstico ya está asignado a otro diagnóstico padre");
            }

            subDiagnostic.setParentDiagnostic(parentDiagnostic);
            MedicalDiagnostic updated = medicalDiagnosticRepository.save(subDiagnostic);

            return ApiResponse.success(medicalDiagnosticMapper.toResponseDTO(updated), "Sub-diagnóstico agregado exitosamente al diagnóstico padre");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<MedicalDiagnosticResponseDTO> removeSubDiagnostic(Integer parentDiagnosticId, Integer subDiagnosticId) {
        try {
            if (parentDiagnosticId == null || subDiagnosticId == null) {
                return ApiResponse.error("Los IDs del diagnóstico padre y del sub-diagnóstico no pueden ser nulos");
            }

            if (!this.medicalDiagnosticRepository.existsById(parentDiagnosticId)) {
                return ApiResponse.error("No se encontró un diagnóstico padre con ID: " + parentDiagnosticId);
            }

            MedicalDiagnostic subDiagnostic = medicalDiagnosticRepository.findById(subDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException("Sub-diagnóstico no encontrado con ID: " + subDiagnosticId));

            if (subDiagnostic.getParentDiagnostic() == null
                    || !subDiagnostic.getParentDiagnostic().getId().equals(parentDiagnosticId)) {
                return ApiResponse.error("Este sub-diagnóstico no pertenece al diagnóstico padre indicado");
            }

            // Validar que el subdiagnostico que se va a remover no este dentro de una configuracion.
            if (this.diagnosticCategoryDatasetRepository.existsByMedicalDiagnosticId(subDiagnosticId)) {
                return ApiResponse.error("No se puede remover este sub-diagnóstico porque está asignado a una categoría dentro de un dataset.");
            }

            subDiagnostic.setParentDiagnostic(null);
            MedicalDiagnostic updated = medicalDiagnosticRepository.save(subDiagnostic);

            return ApiResponse.success(medicalDiagnosticMapper.toResponseDTO(updated), "Sub-diagnóstico removido del diagnóstico padre exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }
}
