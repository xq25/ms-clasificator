package backend.msclasificator.medicalimage.application;

import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeCreateDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeResponseDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeSummaryDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageTypeUpdateDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.medicalimage.api.mapper.MedicalImageTypeMapper;
import backend.msclasificator.doctor.domain.EvaluationArea;
import backend.msclasificator.medicalimage.domain.MedicalImageType;
import backend.msclasificator.doctor.application.port.out.EvaluationAreaRepository;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageTypeRepository;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageRepository;
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
    private MedicalImageRepository medicalImgRepository;

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;

    @Autowired
    private MedicalImageTypeMapper medicalImageTypeMapper;

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalImageTypeSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<MedicalImageTypeSummaryDTO> page = medicalImageTypeRepository.findAll(pageRequest.toPageQuery())
                .map(medicalImageTypeMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<MedicalImageTypeSummaryDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Tipos de imagen obtenidos exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<MedicalImageTypeSummaryDTO>> searchByName(String query, PageRequestDTO pageRequest) {
        try {
            PageResult<MedicalImageTypeSummaryDTO> page = medicalImageTypeRepository
                    .searchByName(query, pageRequest.toPageQuery())
                    .map(medicalImageTypeMapper::toSummaryDTO);

            return ApiResponse.success(
                    PagedResponse.<MedicalImageTypeSummaryDTO>builder()
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
            return ApiResponse.error("Error al buscar tipos de imagen por nombre: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(medicalImageTypeRepository.count(), "Total de tipos de imagen");
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
