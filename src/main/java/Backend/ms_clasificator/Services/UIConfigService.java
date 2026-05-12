package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.UIConfig.CreateUIConfigDTO;
import Backend.ms_clasificator.DTOs.UIConfig.UpdateUIConfigDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.UIConfig.UIConfigMappers;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Models.UIConfig;
import Backend.ms_clasificator.Models.UIState;
import Backend.ms_clasificator.Repositories.MedicalDiagnosticRepository;
import Backend.ms_clasificator.Repositories.UIStateRepository;
import Backend.ms_clasificator.Repositories.UIConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UIConfigService {

    @Autowired
    private UIConfigRepository uiConfigRepository;

    @Autowired
    private UIConfigMappers uiConfigMappers;

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    @Autowired
    private UIStateRepository uiStateRepository;

    /**
     * Obtener todas las configuraciones UI
     * @return Lista de todas las configuraciones
     */
    @Transactional(readOnly = true)
    public List<UIConfig> findAll() {
        return uiConfigRepository.findAll();
    }

    /**
     * Obtener una configuración UI por ID
     * @param id ID de la configuración
     * @return UIConfig encontrada o null
     */
    @Transactional(readOnly = true)
    public UIConfig findById(Integer id) {
        return uiConfigRepository.findById(id).orElse(null);
    }

    /**
     * Obtener todas las configuraciones UI de un diagnóstico médico
     * @param medicalDiagnosticId ID del diagnóstico médico
     * @return Lista de configuraciones del diagnóstico
     */
    @Transactional(readOnly = true)
    public List<UIConfig> findByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        return uiConfigRepository.findByMedicalDiagnosticId(medicalDiagnosticId);
    }

    /**
     * Crear una nueva configuración UI
     * @param createUIConfigDTO DTO con datos de entrada
     * @return ApiResponse<UIConfig> con el resultado de la operación
     */
    public ApiResponse<UIConfig> create(CreateUIConfigDTO createUIConfigDTO) {
        try {
            if (createUIConfigDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que exista el diagnóstico médico
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository
                    .findById(createUIConfigDTO.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Diagnóstico médico no encontrado con ID: " + createUIConfigDTO.getMedicalDiagnosticId()));

            // Validar que no exista ya una configuración para este diagnóstico
            if (uiConfigRepository.existsByMedicalDiagnosticId(createUIConfigDTO.getMedicalDiagnosticId())) {
                return ApiResponse.error("Ya existe una configuración UI para este diagnóstico médico");
            }

            UIConfig uiConfig = uiConfigMappers.toEntity(createUIConfigDTO);
            uiConfig.setMedicalDiagnostic(medicalDiagnostic);

            UIConfig saved = uiConfigRepository.save(uiConfig);
            return ApiResponse.success(saved, "Configuración UI creada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al crear configuración UI: " + ex.getMessage());
        }
    }

    /**
     * Actualizar una configuración UI existente
     * @param id ID de la configuración a actualizar
     * @param updateUIConfigDTO DTO con datos a actualizar
     * @return ApiResponse<UIConfig> con el resultado de la operación
     */
    public ApiResponse<UIConfig> update(Integer id, UpdateUIConfigDTO updateUIConfigDTO) {
        try {
            if (updateUIConfigDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            UIConfig uiConfig = uiConfigRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Configuración UI no encontrada con ID: " + id));

            // Validar que exista el nuevo diagnóstico médico
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository
                    .findById(updateUIConfigDTO.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Diagnóstico médico no encontrado con ID: " + updateUIConfigDTO.getMedicalDiagnosticId()));

            // Validar que no exista otra config para este diagnóstico (evitar duplicados)
            if (!uiConfig.getMedicalDiagnostic().getId().equals(updateUIConfigDTO.getMedicalDiagnosticId())) {
                if (uiConfigRepository.existsByMedicalDiagnosticId(updateUIConfigDTO.getMedicalDiagnosticId())) {
                    return ApiResponse.error("Ya existe una configuración UI para este diagnóstico médico");
                }
            }

            uiConfig.setMedicalDiagnostic(medicalDiagnostic);

            UIConfig updated = uiConfigRepository.save(uiConfig);
            return ApiResponse.success(updated, "Configuración UI actualizada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar configuración UI: " + ex.getMessage());
        }
    }

    /**
     * Eliminar una configuración UI por ID
     * Nota: Los UIState asociados se eliminarán automáticamente (cascade)
     * @param id ID de la configuración a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<Void> delete(Integer id) {
        try {
            UIConfig uiConfig = uiConfigRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Configuración UI no encontrada con ID: " + id));

            uiConfigRepository.delete(uiConfig);
            return ApiResponse.success("Configuración UI eliminada exitosamente (y sus UIStates asociados)");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar configuración UI: " + ex.getMessage());
        }
    }

    /**
     * Agregar un UIState existente a una configuración UI
     * @param uiConfigId ID de la configuración UI
     * @param uiStateId ID del UIState a agregar
     * @return ApiResponse<UIState> con el estado actualizado
     */
    public ApiResponse<UIState> addUIState(Integer uiConfigId, Integer uiStateId) {
        try {
            if (uiConfigId == null || uiStateId == null) {
                return ApiResponse.error("Los IDs de la configuración UI y del estado no pueden ser nulos");
            }

            // Validar que exista la configuración UI
            UIConfig uiConfig = uiConfigRepository.findById(uiConfigId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Configuración UI no encontrada con ID: " + uiConfigId));

            // Validar que exista el UIState
            UIState uiState = uiStateRepository.findById(uiStateId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Estado UI no encontrado con ID: " + uiStateId));

            // Asociar el UIState a la configuración
            uiState.setUiConfig(uiConfig);
            UIState updated = uiStateRepository.save(uiState);

            return ApiResponse.success(updated, "UIState agregado a la configuración exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al agregar UIState a la configuración: " + ex.getMessage());
        }
    }
}
