package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeResponseDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeSummaryDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeUpdateDTO;
import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import org.springframework.data.domain.Page;
import Backend.ms_clasificator.Mappers.MedicalImageTypeMappers.MedicalImageTypeMapper;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Models.MedicalImageType;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import Backend.ms_clasificator.Repositories.MedicalImageTypeRepository;
import Backend.ms_clasificator.Repositories.MedicalImgRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicalImageTypeService {
    @Autowired
    private MedicalImageTypeRepository medicalImageTypeRepository;

    @Autowired
    private MedicalImgRepository medicalImgRepository;

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;

    @Autowired
    private MedicalImageTypeMapper medicalImageTypeMapper;

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalImageTypeSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        Page<MedicalImageTypeSummaryDTO> page = medicalImageTypeRepository.findAll(pageRequest.toPageable())
                .map(medicalImageTypeMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<MedicalImageTypeSummaryDTO>builder()
                        .content(page.getContent())
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .last(page.isLast())
                        .build(),
                "Tipos de imagen obtenidos exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalImageTypeSummaryDTO>> searchByName(String query, PageRequestDTO pageRequest) {
        try {
            Page<MedicalImageTypeSummaryDTO> page = medicalImageTypeRepository
                    .findByNameContainingIgnoreCase(query, pageRequest.toPageable())
                    .map(medicalImageTypeMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<MedicalImageTypeSummaryDTO>builder()
                            .content(page.getContent())
                            .page(page.getNumber())
                            .size(page.getSize())
                            .totalElements(page.getTotalElements())
                            .totalPages(page.getTotalPages())
                            .last(page.isLast())
                            .build(),
                    "Resultados de búsqueda por nombre"
            );
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar tipos de imagen por nombre: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(medicalImageTypeRepository.countAll(), "Total de tipos de imagen");
    }

    @Transactional(readOnly = true)
    public ApiResponse<MedicalImageTypeResponseDTO> findById(Integer id) {
        try {
            MedicalImageType medicalImageType = medicalImageTypeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de imagen médica no encontrado con ID: " + id));

            return ApiResponse.success(medicalImageTypeMapper.toResponseDTO(medicalImageType), "Tipo de imagen obtenido exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<MedicalImageTypeSummaryDTO>> findByEvaluationAreaId(Integer evaluationAreaId){
        if(!evaluationAreaRepository.existsById(evaluationAreaId)){
            return ApiResponse.error("Área de evaluación no encontrada con ID: " + evaluationAreaId);
        }

        List<MedicalImageTypeSummaryDTO> imageTypes = this.medicalImageTypeRepository.findByEvaluationAreaId(evaluationAreaId)
                .stream().map(medicalImageTypeMapper::toSummaryDTO)
                .toList();

        if (imageTypes.isEmpty()){
            return ApiResponse.success(imageTypes, "No se encontraron tipos de imagen asociados al área de evaluación con ID: " + evaluationAreaId);
        }
        return ApiResponse.success(imageTypes, "Tipos de imagen asociados al área de evaluación con ID: " + evaluationAreaId + " obtenidos exitosamente");

    }

    public ApiResponse<MedicalImageTypeResponseDTO> create(MedicalImageTypeCreateDTO dto) {

        if (dto == null) {
            return ApiResponse.error("El DTO no puede ser nulo");
        }

        // Validamos el que el nombre sea unico
        if (this.medicalImageTypeRepository.existsByName(dto.getName())) {
            return ApiResponse.error("Ya existe un tipo de imagen con el nombre: " + dto.getName());
        }

        MedicalImageType medicalImageType = medicalImageTypeMapper.toEntity(dto);
        return ApiResponse.success(medicalImageTypeMapper.toResponseDTO(this.medicalImageTypeRepository.save(medicalImageType)), "Tipo de imagen creado exitosamente");

    }

    @Transactional
    public ApiResponse<MedicalImageTypeResponseDTO> update(Integer id, MedicalImageTypeUpdateDTO dto) {
        try {
            if (dto == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            MedicalImageType medicalImageType = medicalImageTypeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de imagen médica no encontrado con ID: " + id));

            // Validamos el que el nombre sea unico
            if (this.medicalImageTypeRepository.existsByName(dto.getName())) {
                return ApiResponse.error("Ya existe un tipo de imagen con el nombre: " + dto.getName());
            }
            medicalImageType.setName(dto.getName());

            return ApiResponse.success(medicalImageTypeMapper.toResponseDTO(medicalImageTypeRepository.save(medicalImageType)), "Tipo de imagen actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            MedicalImageType medicalImageType = medicalImageTypeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de imagen médica no encontrado con ID: " + id));

            if (medicalImgRepository.existsByMedicalImageTypeId(id)) {
                return ApiResponse.error("No se puede eliminar el tipo de imagen porque tiene imágenes médicas asociadas");
            }

            medicalImageTypeRepository.delete(medicalImageType);
            return ApiResponse.success("Tipo de imagen eliminado exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violacion de integridad de la base de datos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    // Asignar area de evaluacion
    @Transactional
    public ApiResponse<MedicalImageTypeResponseDTO> assignEvaluationArea(Integer medicalImageTypeId, Integer evaluationAreaId) {
        try {
            MedicalImageType medicalImageType = medicalImageTypeRepository.findById(medicalImageTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de imagen médica no encontrado con ID: " + medicalImageTypeId));

            EvaluationArea evaluationArea = evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + evaluationAreaId));

            medicalImageType.setEvaluationArea(evaluationArea);
            return ApiResponse.success(medicalImageTypeMapper.toResponseDTO(medicalImageTypeRepository.save(medicalImageType)), "Área de evaluación asignada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    //Remover area de evaluacion
    public ApiResponse<MedicalImageTypeResponseDTO> removeEvaluationArea(Integer medicalImageTypeId) {
        try {
            MedicalImageType medicalImageType = medicalImageTypeRepository.findById(medicalImageTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de imagen médica no encontrado con ID: " + medicalImageTypeId));

            //Validamos que si tenga un area de evaluacion que quitar
            if (medicalImageType.getEvaluationArea() == null) {
                return ApiResponse.error("El tipo de imagen médica no tiene un área de evaluación asignada para ser eliminada");
            }

            medicalImageType.setEvaluationArea(null);
            return ApiResponse.success(medicalImageTypeMapper.toResponseDTO(medicalImageTypeRepository.save(medicalImageType)), "Área de evaluación removida exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }
}
