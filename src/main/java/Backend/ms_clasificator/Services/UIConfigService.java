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
    public ApiResponse<UIConfig> findById(Integer id) {
        try {
            UIConfig uiConfig = uiConfigRepository.findById(id).orElseThrow(() ->
                    new IllegalArgumentException("Configuración UI no encontrada con ID: " + id));
            return ApiResponse.success(uiConfig, "Configuración UI encontrada");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar configuración por ID: " + ex.getMessage());
        }
    }

    /**
     * Obtener todas las configuraciones UI de un diagnóstico médico
     * @param medicalDiagnosticId ID del diagnóstico médico
     * @return Lista de configuraciones del diagnóstico
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<UIConfig>> findByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        try{
            MedicalDiagnostic medicalDiagnostic = this.medicalDiagnosticRepository.findById(medicalDiagnosticId).orElseThrow(() ->
                    new IllegalArgumentException("Diagnóstico médico no encontrado con ID: " + medicalDiagnosticId));

            List<UIConfig> uiConfigs = uiConfigRepository.findByMedicalDiagnostic_Id(medicalDiagnosticId);

            if (uiConfigs.isEmpty()) {
                return ApiResponse.success(uiConfigs,"No se encontraron configuraciones UI para el diagnóstico médico con ID: " + medicalDiagnosticId);
            }else{
                return ApiResponse.success(uiConfigs,"Configuraciones UI encontradas para el diagnóstico médico con ID: " + medicalDiagnosticId);
            }

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar configuraciones por ID de diagnóstico médico: " + ex.getMessage());
        }
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
            if (uiConfigRepository.existsByMedicalDiagnostic_Id(createUIConfigDTO.getMedicalDiagnosticId())) {
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
    @Transactional
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

            // Validamos que no exista ya una configuracion para ese diagnostico
            if (uiConfigRepository.existsByMedicalDiagnostic_Id(updateUIConfigDTO.getMedicalDiagnosticId())) {
                return ApiResponse.error("Ya existe una configuración UI para este diagnóstico médico");
            }

            // Validar que el diagnostico nuevo no sea este en uno de los states (Rompe la regla de que una configuracion no puede clasificar una misma enfermdad o diagnostico de si misma)
            if (!uiConfig.getUiStates().isEmpty()) {
                uiConfig.getUiStates().forEach(state -> {
                    if (state.getMedicalDiagnostic().getId().equals(updateUIConfigDTO.getMedicalDiagnosticId())) {
                        throw new IllegalArgumentException("El diagnóstico médico para el que se diseña la configuración no puede ser el mismo que el diagnóstico médico de alguno de los estados UI asociados");
                    }
                });
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


}
