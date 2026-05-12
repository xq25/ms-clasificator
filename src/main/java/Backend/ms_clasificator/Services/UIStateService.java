package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.UIState.CreateUIStateDTO;
import Backend.ms_clasificator.DTOs.UIState.UpdateUIStateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.UIState.UIStateMappers;
import Backend.ms_clasificator.Models.UIConfig;
import Backend.ms_clasificator.Models.UIState;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
import Backend.ms_clasificator.Repositories.UIStateRepository;
import Backend.ms_clasificator.Repositories.UIConfigRepository;
import Backend.ms_clasificator.Repositories.MedicalDiagnosticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UIStateService {

    @Autowired
    private UIStateRepository uiStateRepository;

    @Autowired
    private UIStateMappers uiStateMappers;

    @Autowired
    private UIConfigRepository uiConfigRepository;

    @Autowired
    private MedicalDiagnosticRepository medicalDiagnosticRepository;

    /**
     * Obtener todos los estados UI
     * @return Lista de todos los estados
     */
    @Transactional(readOnly = true)
    public List<UIState> findAll() {
        return uiStateRepository.findAll();
    }

    /**
     * Obtener un estado UI por ID
     * @param id ID del estado
     * @return UIState encontrado o null
     */
    @Transactional(readOnly = true)
    public UIState findById(Integer id) {
        return uiStateRepository.findById(id).orElse(null);
    }

    /**
     * Obtener todos los estados UI de una configuración
     * @param uiConfigId ID de la configuración
     * @return Lista de estados de la configuración
     */
    @Transactional(readOnly = true)
    public List<UIState> findByUiConfigId(Integer uiConfigId) {
        return uiStateRepository.findByUiConfigId(uiConfigId);
    }

    /**
     * Obtener todos los estados UI de un diagnóstico
     * @param medicalDiagnosticId ID del diagnóstico
     * @return Lista de estados del diagnóstico
     */
    @Transactional(readOnly = true)
    public List<UIState> findByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        return uiStateRepository.findByMedicalDiagnosticId(medicalDiagnosticId);
    }

    /**
     * Crear un nuevo estado UI
     * @param createUIStateDTO DTO con datos de entrada
     * @return ApiResponse<UIState> con el resultado de la operación
     */
    public ApiResponse<UIState> create(CreateUIStateDTO createUIStateDTO) {
        try {
            if (createUIStateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que exista la configuración UI
            UIConfig uiConfig = uiConfigRepository.findById(createUIStateDTO.getIuConfigId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Configuración UI no encontrada con ID: " + createUIStateDTO.getIuConfigId()));

            // Validar que exista el diagnóstico médico
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository
                    .findById(createUIStateDTO.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Diagnóstico médico no encontrado con ID: " + createUIStateDTO.getMedicalDiagnosticId()));

            UIState uiState = uiStateMappers.toEntity(createUIStateDTO);
            uiState.setUiConfig(uiConfig);
            uiState.setMedicalDiagnostic(medicalDiagnostic);

            UIState saved = uiStateRepository.save(uiState);
            return ApiResponse.success(saved, "Estado UI creado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al crear estado UI: " + ex.getMessage());
        }
    }

    /**
     * Actualizar un estado UI existente
     * @param id ID del estado a actualizar
     * @param updateUIStateDTO DTO con datos a actualizar
     * @return ApiResponse<UIState> con el resultado de la operación
     */
    public ApiResponse<UIState> update(Integer id, UpdateUIStateDTO updateUIStateDTO) {
        try {
            if (updateUIStateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            UIState uiState = uiStateRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Estado UI no encontrado con ID: " + id));

            // Validar que exista el diagnóstico médico
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository
                    .findById(updateUIStateDTO.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Diagnóstico médico no encontrado con ID: " + updateUIStateDTO.getMedicalDiagnosticId()));

            // Solo actualizamos el diagnóstico, la configuración UI no se modifica
            uiState.setMedicalDiagnostic(medicalDiagnostic);

            UIState updated = uiStateRepository.save(uiState);
            return ApiResponse.success(updated, "Estado UI actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar estado UI: " + ex.getMessage());
        }
    }

    /**
     * Eliminar un estado UI por ID
     * @param id ID del estado a eliminar
     * @return ApiResponse con el resultado de la operación
     */
    public ApiResponse<Void> delete(Integer id) {
        try {
            UIState uiState = uiStateRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Estado UI no encontrado con ID: " + id));

            uiStateRepository.delete(uiState);
            return ApiResponse.success("Estado UI eliminado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar estado UI: " + ex.getMessage());
        }
    }
}
