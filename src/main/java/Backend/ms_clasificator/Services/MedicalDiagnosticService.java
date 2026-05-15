package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticCreateDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.MedicalDiagnosticMappers.MedicalDiagnosticMapper;
import Backend.ms_clasificator.Models.ImageDiagnostic;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Models.UIState;
import Backend.ms_clasificator.Repositories.ImageDiagnosticRepository;
import Backend.ms_clasificator.Repositories.MedicalDiagnosticRepository;
import Backend.ms_clasificator.Repositories.UIStateRepository;
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
    private ImageDiagnosticRepository imageDiagnosticRepository;

    @Autowired
    private MedicalDiagnosticMapper medicalDiagnosticMapper;

    @Autowired
    private UIStateRepository uiStateRepository;

    /**
     * Obtener todos los diagnósticos médicos
     * @return Lista de todos los diagnósticos
     */
    @Transactional(readOnly = true)
    public List<MedicalDiagnostic> findAll() {
        return medicalDiagnosticRepository.findAll();
    }

    /**
     * Obtener un diagnóstico médico por ID
     * @param id ID del diagnóstico
     * @return Diagnóstico encontrado o null
     */
    @Transactional(readOnly = true)
    public ApiResponse<MedicalDiagnostic> findById(Integer id) {
        try {
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(id).orElseThrow(() ->
                    new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + id));
            return ApiResponse.success(medicalDiagnostic, "Diagnóstico médico encontrado");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar diagnóstico por ID: " + ex.getMessage());
        }
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
     * @param medicalDiagnosticUpdateDTO DTO con datos a actualizar
     * @return ApiResponse<MedicalDiagnostic> con el resultado de la operación
     */
    public ApiResponse<MedicalDiagnostic> update(Integer id, MedicalDiagnosticUpdateDTO medicalDiagnosticUpdateDTO) {
        try {
            if (medicalDiagnosticUpdateDTO == null) {
                throw new IllegalArgumentException("El DTO no puede ser nulo");
            }

            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + id));

            // Validar que no exista otro diagnóstico con el mismo código
            MedicalDiagnostic existingByCode = medicalDiagnosticRepository.findByDiagnosticCode(medicalDiagnosticUpdateDTO.getDiagnosticCode());
            if (existingByCode != null && !existingByCode.getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe un diagnóstico con el código: " + medicalDiagnosticUpdateDTO.getDiagnosticCode());
            }

            medicalDiagnostic.setDiagnosticCode(medicalDiagnosticUpdateDTO.getDiagnosticCode());
            medicalDiagnostic.setDiagnosticName(medicalDiagnosticUpdateDTO.getDiagnosticName());

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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse<Void> delete(Integer id) {

        try {
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + id));

            List<MedicalDiagnostic> subDiagnosticList = medicalDiagnosticRepository.findByParentDiagnostic_Id(id);
            if (!subDiagnosticList.isEmpty()) {
                return ApiResponse.error("El diagnóstico médico no se puede eliminar porque tiene sub-diagnósticos asociados. Elimine o reasigne los sub-diagnósticos antes de eliminar este diagnóstico.");
            }

            List<ImageDiagnostic> imageDiagnosticList = imageDiagnosticRepository.findByMedicalDiagnostic_Id(id);
            if(!imageDiagnosticList.isEmpty()) {
                return ApiResponse.error("El diagnóstico médico no se puede eliminar porque tiene diagnósticos de imagen asociados. Elimine o reasigne los diagnósticos de imagen antes de eliminar este diagnóstico.");
            }

            medicalDiagnosticRepository.delete(medicalDiagnostic);
            return ApiResponse.success("Diagnóstico médico eliminado exitosamente");

        } catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violacion a la integridad de la base de datos:" + ex.getMessage());
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
            if (subDiagnosticId.equals(parentDiagnosticId)) {
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

            // Validar que el dianostico padre exista
            MedicalDiagnostic parentDiagnostic = medicalDiagnosticRepository.findById(parentDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnóstico padre no encontrado con ID: " + parentDiagnosticId));


            // Obtener el sub-diagnóstico
            MedicalDiagnostic subDiagnostic = medicalDiagnosticRepository.findById(subDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException("Sub-diagnóstico no encontrado con ID: " + subDiagnosticId));

            // Validar que el sub-diagnóstico tenga el padre indicado
            if (subDiagnostic.getParentDiagnostic() == null || !subDiagnostic.getParentDiagnostic().getId().equals(parentDiagnosticId)) {
                return ApiResponse.error("Este sub-diagnóstico no pertenece al diagnóstico padre indicado");
            }

            // Validar que sub-diagnostico no este dentro de una configuracion en UIState (No podemos remover un subdiagnostico que ya esta asignado a un uiState)
            List<UIState> uiStateList = uiStateRepository.findByMedicalDiagnostic_Id(subDiagnosticId);
            if (!uiStateList.isEmpty()){
                return ApiResponse.error("No se puede remover este sub-diagnóstico porque está asignado a una configuración de UI. Elimine dentro de la Config UI antes de remover este sub-diagnóstico.");
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
