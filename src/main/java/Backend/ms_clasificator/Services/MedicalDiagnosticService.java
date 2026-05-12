package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.MedicalDiagnosticMappers.MedicalDiagnosticMapper;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Repositories.MedicalDiagnosticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalDiagnosticService {

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    /**
     * Obtener todos los diagnósticos médicos
     * @return Lista de todos los diagnósticos
     */
    public List<MedicalDiagnostic> findAll() {
        return medicalDiagnosticRepository.findAll();
    }

    /**
     * Obtener un diagnóstico médico por ID
     * @param id ID del diagnóstico
     * @return Diagnóstico encontrado o null
     */
    public MedicalDiagnostic findById(Integer id) {
        return medicalDiagnosticRepository.findById(id).orElse(null);
    }

    /**
     * Crear un nuevo diagnóstico médico
     * @param medicalDiagnosticCreateDTO DTO con datos de entrada
     * @return ApiResponse<MedicalDiagnostic> con el resultado de la operación
     */
    public ApiResponse<MedicalDiagnostic> create(MedicalDiagnosticCreateDTO medicalDiagnosticCreateDTO) {
        try {
            if (medicalDiagnosticCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que no exista diagnóstico con el mismo código
            if (medicalDiagnosticRepository.findByDiagnosticCode(medicalDiagnosticCreateDTO.getDiagnosticCode()) != null) {
                return ApiResponse.error("Ya existe un diagnóstico con el código: " + medicalDiagnosticCreateDTO.getDiagnosticCode());
            }


            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticMapper.toEntity(medicalDiagnosticCreateDTO);

            // Si se proporciona un parentDiagnosticId, validar que exista
            if (medicalDiagnosticCreateDTO.getParentDiagnosticId() != null) {
                MedicalDiagnostic parentDiagnostic = medicalDiagnosticRepository.findById(medicalDiagnosticCreateDTO.getParentDiagnosticId())
                        .orElseThrow(() -> new IllegalArgumentException("Diagnóstico padre no encontrado con ID: " + medicalDiagnosticCreateDTO.getParentDiagnosticId()));
                medicalDiagnostic.setParentDiagnostic(parentDiagnostic);
            }

            MedicalDiagnostic saved = medicalDiagnosticRepository.save(medicalDiagnostic);
            return ApiResponse.success(saved, "Diagnóstico médico creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al crear diagnóstico médico: " + ex.getMessage());
        }
    }

    /**
     * Actualizar un diagnóstico médico existente
     * @param id ID del diagnóstico a actualizar
     * @param medicalDiagnosticCreateDTO DTO con datos a actualizar
     * @return ApiResponse<MedicalDiagnostic> con el resultado de la operación
     */
    public ApiResponse<MedicalDiagnostic> update(Integer id, MedicalDiagnosticCreateDTO medicalDiagnosticCreateDTO) {
        try {
            if (medicalDiagnosticCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + id));

            // Validar que no exista otro diagnóstico con el mismo código
            MedicalDiagnostic existingByCode = medicalDiagnosticRepository.findByDiagnosticCode(medicalDiagnosticCreateDTO.getDiagnosticCode());
            if (existingByCode != null && !existingByCode.getId().equals(id)) {
                return ApiResponse.error("Ya existe un diagnóstico con el código: " + medicalDiagnosticCreateDTO.getDiagnosticCode());
            }

            medicalDiagnostic.setDiagnosticCode(medicalDiagnosticCreateDTO.getDiagnosticCode());
            medicalDiagnostic.setDiagnosticName(medicalDiagnosticCreateDTO.getDiagnosticName());

            // Actualizar diagnóstico padre si se proporciona
            if (medicalDiagnosticCreateDTO.getParentDiagnosticId() != null) {
                MedicalDiagnostic parentDiagnostic = medicalDiagnosticRepository.findById(medicalDiagnosticCreateDTO.getParentDiagnosticId())
                        .orElseThrow(() -> new IllegalArgumentException("Diagnóstico padre no encontrado con ID: " + medicalDiagnosticCreateDTO.getParentDiagnosticId()));
                medicalDiagnostic.setParentDiagnostic(parentDiagnostic);
            } else {
                medicalDiagnostic.setParentDiagnostic(null);
            }

            MedicalDiagnostic updated = medicalDiagnosticRepository.save(medicalDiagnostic);
            return ApiResponse.success(updated, "Diagnóstico médico actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar diagnóstico médico: " + ex.getMessage());
        }
    }

    /**
     * Eliminar un diagnóstico médico por ID
     * @param id ID del diagnóstico a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<Void> delete(Integer id) {
        try {
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + id));

            medicalDiagnosticRepository.delete(medicalDiagnostic);
            return ApiResponse.success("Diagnóstico médico eliminado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar diagnóstico médico: " + ex.getMessage());
        }
    }

    // ===== Relaciones con otras entidades (Relación padre-hijo) =====

    /**
     * Agregar un sub-diagnóstico a un diagnóstico padre
     * @param parentDiagnosticId ID del diagnóstico padre
     * @param subDiagnosticId ID del sub-diagnóstico a agregar
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<MedicalDiagnostic> addSubDiagnostic(Integer parentDiagnosticId, Integer subDiagnosticId) {
        try {
            if (parentDiagnosticId == null || subDiagnosticId == null) {
                return ApiResponse.error("Los IDs del diagnóstico padre y del sub-diagnóstico no pueden ser nulos");
            }

            // Validar que no sea el mismo diagnóstico
            if (parentDiagnosticId.equals(subDiagnosticId)) {
                return ApiResponse.error("Un diagnóstico no puede ser sub-diagnóstico de sí mismo");
            }

            // Obtener el diagnóstico padre
            MedicalDiagnostic parentDiagnostic = medicalDiagnosticRepository.findById(parentDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico padre no encontrado con ID: " + parentDiagnosticId));

            // Obtener el sub-diagnóstico
            MedicalDiagnostic subDiagnostic = medicalDiagnosticRepository.findById(subDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException("Sub-diagnóstico no encontrado con ID: " + subDiagnosticId));

            // Validar que el sub-diagnóstico no tenga ya un padre diferente
            if (subDiagnostic.getParentDiagnostic() != null && !subDiagnostic.getParentDiagnostic().getId().equals(parentDiagnosticId)) {
                throw new IllegalArgumentException("Este sub-diagnóstico ya está asignado a otro diagnóstico padre");
            }
            // Validar que un diagnostico no puede ser sub-diagnóstico de sí mismo (esto es para evitar ciclos en la jerarquía)
            if (subDiagnosticId == parentDiagnosticId){
                throw  new IllegalArgumentException("Un diagnóstico no puede ser sub-diagnóstico de sí mismo");
            }

            // Asignar el padre al sub-diagnóstico
            subDiagnostic.setParentDiagnostic(parentDiagnostic);
            MedicalDiagnostic updated = medicalDiagnosticRepository.save(subDiagnostic);

            return ApiResponse.success(updated, "Sub-diagnóstico agregado exitosamente al diagnóstico padre");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al agregar sub-diagnóstico: " + ex.getMessage());
        }
    }

    /**
     * Remover un sub-diagnóstico de un diagnóstico padre
     * @param parentDiagnosticId ID del diagnóstico padre
     * @param subDiagnosticId ID del sub-diagnóstico a remover
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<MedicalDiagnostic> removeSubDiagnostic(Integer parentDiagnosticId, Integer subDiagnosticId) {
        try {
            if (parentDiagnosticId == null || subDiagnosticId == null) {
                return ApiResponse.error("Los IDs del diagnóstico padre y del sub-diagnóstico no pueden ser nulos");
            }

            // Obtener el sub-diagnóstico
            MedicalDiagnostic subDiagnostic = medicalDiagnosticRepository.findById(subDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException("Sub-diagnóstico no encontrado con ID: " + subDiagnosticId));

            // Validar que el sub-diagnóstico tenga el padre indicado
            if (subDiagnostic.getParentDiagnostic() == null || !subDiagnostic.getParentDiagnostic().getId().equals(parentDiagnosticId)) {
                return ApiResponse.error("Este sub-diagnóstico no pertenece al diagnóstico padre indicado");
            }

            // Remover el padre del sub-diagnóstico
            subDiagnostic.setParentDiagnostic(null);
            MedicalDiagnostic updated = medicalDiagnosticRepository.save(subDiagnostic);

            return ApiResponse.success(updated, "Sub-diagnóstico removido del diagnóstico padre exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al remover sub-diagnóstico: " + ex.getMessage());
        }
    }
}
