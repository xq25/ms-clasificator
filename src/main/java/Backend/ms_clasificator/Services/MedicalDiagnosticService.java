package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticResponseDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.MedicalDiagnosticMappers.MedicalDiagnosticMapper;
import Backend.ms_clasificator.Models.DiagnosticCategoryDataset;
import Backend.ms_clasificator.Models.ImageDoctorDiagnostics;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Repositories.DiagnosticCategoryDatasetRepository;
import Backend.ms_clasificator.Repositories.ImageDoctorDiagnosticsRepository;
import Backend.ms_clasificator.Repositories.MedicalDiagnosticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicalDiagnosticService {

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Autowired
    private ImageDoctorDiagnosticsRepository imageDoctorDiagnosticsRepository;

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Autowired
    private DiagnosticCategoryDatasetRepository diagnosticCategoryDatasetRepository;



    /**
     * Obtener todos los diagnósticos médicos
     * @return Lista de todos los diagnósticos
     */
    @Transactional(readOnly = true)
    public List<MedicalDiagnosticResponseDTO> findAll() {
        return medicalDiagnosticRepository.findAll()
                .stream()
                .map(medicalDiagnosticMapper::toResponseDTO)
                .toList();
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
    public ApiResponse<List<MedicalDiagnosticResponseDTO>> findByParentDiagnosticId(Integer parentDiagnosticId) {
        try {
            List<MedicalDiagnosticResponseDTO> diagnostics = medicalDiagnosticRepository
                    .findByParentDiagnostic_Id(parentDiagnosticId)
                    .stream()
                    .map(medicalDiagnosticMapper::toResponseDTO)
                    .toList();
            return ApiResponse.success(diagnostics, "Diagnósticos médicos encontrados para el diagnóstico padre ID: " + parentDiagnosticId);
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar diagnósticos por ID de diagnóstico padre: " + ex.getMessage());
        }
    }

    public ApiResponse<MedicalDiagnosticResponseDTO> create(MedicalDiagnosticCreateDTO medicalDiagnosticCreateDTO) {
        try {
            if (medicalDiagnosticCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            if (medicalDiagnosticRepository.findByDiagnosticCode(medicalDiagnosticCreateDTO.getDiagnosticCode()) != null) {
                return ApiResponse.error("Ya existe un diagnóstico con el código: " + medicalDiagnosticCreateDTO.getDiagnosticCode());
            }

            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticMapper.toEntity(medicalDiagnosticCreateDTO);

            if (medicalDiagnosticCreateDTO.getParentDiagnosticId() != null) {
                MedicalDiagnostic parentDiagnostic = medicalDiagnosticRepository
                        .findById(medicalDiagnosticCreateDTO.getParentDiagnosticId())
                        .orElseThrow(() -> new IllegalArgumentException("Diagnóstico padre no encontrado con ID: "
                                + medicalDiagnosticCreateDTO.getParentDiagnosticId()));
                medicalDiagnostic.setParentDiagnostic(parentDiagnostic);
            }

            MedicalDiagnostic saved = medicalDiagnosticRepository.save(medicalDiagnostic);
            return ApiResponse.success(medicalDiagnosticMapper.toResponseDTO(saved), "Diagnóstico médico creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al crear diagnóstico médico: " + ex.getMessage());
        }
    }

    public ApiResponse<MedicalDiagnosticResponseDTO> update(Integer id, MedicalDiagnosticUpdateDTO medicalDiagnosticUpdateDTO) {
        try {
            if (medicalDiagnosticUpdateDTO == null) {
                throw new IllegalArgumentException("El DTO no puede ser nulo");
            }

            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + id));

            MedicalDiagnostic existingByCode = medicalDiagnosticRepository
                    .findByDiagnosticCode(medicalDiagnosticUpdateDTO.getDiagnosticCode());
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
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar diagnóstico médico: " + ex.getMessage());
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse<Void> delete(Integer id) {
        try {
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + id));

            List<MedicalDiagnostic> subDiagnosticList = medicalDiagnosticRepository.findByParentDiagnostic_Id(id);
            if (!subDiagnosticList.isEmpty()) {
                return ApiResponse.error("El diagnóstico médico no se puede eliminar porque tiene sub-diagnósticos asociados.");
            }

            List<ImageDoctorDiagnostics> imageDoctorDiagnosticsList = imageDoctorDiagnosticsRepository.findByMedicalDiagnosticId(id);
            if (!imageDoctorDiagnosticsList.isEmpty()) {
                return ApiResponse.error("No se puede eliminar el diagnóstico ya que está asociado a clasificaciones de imágenes.");
            }

            medicalDiagnosticRepository.delete(medicalDiagnostic);
            return ApiResponse.success("Diagnóstico médico eliminado exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violación a la integridad de la base de datos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar diagnóstico médico: " + ex.getMessage());
        }
    }

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
        } catch (Exception ex) {
            return ApiResponse.error("Error al agregar sub-diagnóstico: " + ex.getMessage());
        }
    }

    public ApiResponse<MedicalDiagnosticResponseDTO> removeSubDiagnostic(Integer parentDiagnosticId, Integer subDiagnosticId) {
        try {
            if (parentDiagnosticId == null || subDiagnosticId == null) {
                return ApiResponse.error("Los IDs del diagnóstico padre y del sub-diagnóstico no pueden ser nulos");
            }

            medicalDiagnosticRepository.findById(parentDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico padre no encontrado con ID: " + parentDiagnosticId));

            MedicalDiagnostic subDiagnostic = medicalDiagnosticRepository.findById(subDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException("Sub-diagnóstico no encontrado con ID: " + subDiagnosticId));

            if (subDiagnostic.getParentDiagnostic() == null
                    || !subDiagnostic.getParentDiagnostic().getId().equals(parentDiagnosticId)) {
                return ApiResponse.error("Este sub-diagnóstico no pertenece al diagnóstico padre indicado");
            }

            List<DiagnosticCategoryDataset> diagnosticCategoryDatasets =
                    diagnosticCategoryDatasetRepository.findByMedicalDiagnosticId(subDiagnosticId);
            if (!diagnosticCategoryDatasets.isEmpty()) {
                return ApiResponse.error("No se puede remover este sub-diagnóstico porque está asignado a una categoría dentro de un dataset.");
            }

            subDiagnostic.setParentDiagnostic(null);
            MedicalDiagnostic updated = medicalDiagnosticRepository.save(subDiagnostic);

            return ApiResponse.success(medicalDiagnosticMapper.toResponseDTO(updated), "Sub-diagnóstico removido del diagnóstico padre exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al remover sub-diagnóstico: " + ex.getMessage());
        }
    }
}
