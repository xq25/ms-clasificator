package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeResponseDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.MedicalImageTypeMappers.MedicalImageTypeMapper;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Models.MedicalImg;
import Backend.ms_clasificator.Models.MedicalImageType;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import Backend.ms_clasificator.Repositories.MedicalImageTypeRepository;
import Backend.ms_clasificator.Repositories.MedicalImgRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    public ApiResponse<List<MedicalImageTypeResponseDTO>> findAll() {
        try {
            List<MedicalImageTypeResponseDTO> response = medicalImageTypeRepository.findAll()
                    .stream()
                    .map(medicalImageTypeMapper::toResponseDTO)
                    .toList();
            return ApiResponse.success(response, "Tipos de imagen obtenidos exitosamente");
        } catch (Exception ex) {
            return ApiResponse.error("Error al listar tipos de imagen: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<MedicalImageTypeResponseDTO> findById(Integer id) {
        try {
            MedicalImageType medicalImageType = medicalImageTypeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de imagen médica no encontrado con ID: " + id));

            return ApiResponse.success(medicalImageTypeMapper.toResponseDTO(medicalImageType), "Tipo de imagen obtenido exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al obtener tipo de imagen: " + ex.getMessage());
        }
    }

    public ApiResponse<MedicalImageTypeResponseDTO> create(MedicalImageTypeCreateDTO dto) {
        try {
            if (dto == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            MedicalImageType existing = medicalImageTypeRepository.findByName(dto.getName());
            if (existing != null) {
                return ApiResponse.error("Ya existe un tipo de imagen con el nombre: " + dto.getName());
            }

            EvaluationArea evaluationArea = null;
            if (dto.getEvaluationAreaId() != null) {
                evaluationArea = evaluationAreaRepository.findById(dto.getEvaluationAreaId()).orElse(null);
            }
            if (dto.getEvaluationAreaId() != null && evaluationArea == null) {
                return ApiResponse.error("El area de evaluacion asignada no existe, con id " + dto.getEvaluationAreaId());
            }

            MedicalImageType medicalImageType = medicalImageTypeMapper.toEntity(dto);
            medicalImageType.setEvaluationArea(evaluationArea);
            medicalImageType = this.medicalImageTypeRepository.save(medicalImageType);
            return ApiResponse.success(medicalImageTypeMapper.toResponseDTO(medicalImageType), "Tipo de imagen creado exitosamente");

        } catch (Exception ex) {
            return ApiResponse.error("Error al intentar crear un tipo de imagen: " + ex.getMessage());
        }
    }

    public ApiResponse<MedicalImageTypeResponseDTO> update(Integer id, MedicalImageTypeUpdateDTO dto) {
        try {
            if (dto == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            MedicalImageType medicalImageType = medicalImageTypeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de imagen médica no encontrado con ID: " + id));

            if (dto.getName() != null) {
                MedicalImageType existing = medicalImageTypeRepository.findByName(dto.getName());
                if (existing != null && !existing.getId().equals(id)) {
                    return ApiResponse.error("Ya existe un tipo de imagen con el nombre: " + dto.getName());
                }
                medicalImageType.setName(dto.getName());
            }

            MedicalImageType updated = medicalImageTypeRepository.save(medicalImageType);
            return ApiResponse.success(medicalImageTypeMapper.toResponseDTO(updated), "Tipo de imagen actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar tipo de imagen: " + ex.getMessage());
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse<Void> delete(Integer id) {
        try {
            MedicalImageType medicalImageType = medicalImageTypeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de imagen médica no encontrado con ID: " + id));

            List<MedicalImg> medicalImgs = medicalImgRepository.findByMedicalImageTypeId(id);
            if (!medicalImgs.isEmpty()) {
                return ApiResponse.error("No se puede eliminar el tipo de imagen porque tiene imágenes médicas asociadas");
            }

            medicalImageTypeRepository.delete(medicalImageType);
            return ApiResponse.success("Tipo de imagen eliminado exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violacion de integridad de la base de datos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar tipo de imagen: " + ex.getMessage());
        }
    }

    // Asignar area de evaluacion
    public ApiResponse<MedicalImageTypeResponseDTO> assignEvaluationArea(Integer medicalImageTypeId, Integer evaluationAreaId) {
        try {
            MedicalImageType medicalImageType = medicalImageTypeRepository.findById(medicalImageTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de imagen médica no encontrado con ID: " + medicalImageTypeId));

            EvaluationArea evaluationArea = evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + evaluationAreaId));

            medicalImageType.setEvaluationArea(evaluationArea);
            MedicalImageType updated = medicalImageTypeRepository.save(medicalImageType);
            return ApiResponse.success(medicalImageTypeMapper.toResponseDTO(updated), "Área de evaluación asignada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al asignar área de evaluación: " + ex.getMessage());
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
            MedicalImageType updated = medicalImageTypeRepository.save(medicalImageType);
            return ApiResponse.success(medicalImageTypeMapper.toResponseDTO(updated), "Área de evaluación removida exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al remover área de evaluación: " + ex.getMessage());
        }
    }
}
