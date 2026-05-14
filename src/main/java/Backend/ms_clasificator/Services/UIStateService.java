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
    public ApiResponse<UIState> findById(Integer id) {
        try {
            UIState uiState =  uiStateRepository.findById(id).orElseThrow(() ->
                    new IllegalArgumentException("Estado UI no encontrado con ID: " + id));

            return ApiResponse.success(uiState, "Estado UI encontrado");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar estado por ID: " + ex.getMessage());
        }
    }

    /**
     * Obtener todos los estados UI de una configuración
     * @param uiConfigId ID de la configuración
     * @return Lista de estados de la configuración
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<UIState>> findByUiConfigId(Integer uiConfigId) {
        try {
            UIConfig uiConfig = this.uiConfigRepository.findById(uiConfigId).orElseThrow(() ->
                    new IllegalArgumentException("Configuración UI no encontrada con ID: " + uiConfigId));

            List<UIState> uiStates = this.uiStateRepository.findByUiConfig_Id(uiConfigId);
            if (uiStates.isEmpty()) {
                return ApiResponse.success(uiStates,"No se encontraron estados UI para la configuración con ID: " + uiConfigId);
            }else{
                return ApiResponse.success(uiStates,"Estados UI encontrados para la configuración con ID: " + uiConfigId);
            }

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar estado por ID: " + ex.getMessage());
        }
    }

    /**
     * Obtener todos los estados UI de un diagnóstico
     * @param medicalDiagnosticId ID del diagnóstico
     * @return Lista de estados del diagnóstico
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<UIState>> findByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        try {

            MedicalDiagnostic medicalDiagnostic = this.medicalDiagnosticRepository.findById(medicalDiagnosticId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Diagnóstico médico no encontrado con ID: " + medicalDiagnosticId));

            List<UIState> states = uiStateRepository.findByMedicalDiagnostic_Id(medicalDiagnosticId);
            if (states.isEmpty()) {
                return ApiResponse.success(states, "No se encontraron estados UI para el diagnóstico con ID: " + medicalDiagnosticId);
            } else {
                return ApiResponse.success(states, "Estados UI encontrados para el diagnóstico");
            }
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar estados por diagnóstico: " + ex.getMessage());
        }

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
            UIConfig uiConfig = uiConfigRepository.findById(createUIStateDTO.getUiConfigId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Configuración UI no encontrada con ID: " + createUIStateDTO.getUiConfigId()));

            // Validar que exista el diagnóstico médico
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository
                    .findById(createUIStateDTO.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Diagnóstico médico no encontrado con ID: " + createUIStateDTO.getMedicalDiagnosticId()));

            // Validar que el diagnostico asignado a este uistate no sea el mismo de el uiconfig
            if (medicalDiagnostic.getId().equals(uiConfig.getMedicalDiagnostic().getId())) {
                return ApiResponse.error("El diagnóstico médico del estado UI no puede ser el mismo que el de la configuración UI, No podemos clasificar un diagnostico con su mismo diagnostico" );
            }

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

            // Hayamos la configuración UI para validar que exista y obtener su diagnóstico
            UIConfig uiConfig = this.uiConfigRepository.findById(uiState.getUiConfigId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Configuración UI a la que pertence este state, no encontrada con ID: " + uiState.getUiConfigId()));

            // Validar que exista el diagnóstico médico
            MedicalDiagnostic medicalDiagnostic = medicalDiagnosticRepository
                    .findById(updateUIStateDTO.getMedicalDiagnosticId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Diagnóstico médico no encontrado con ID: " + updateUIStateDTO.getMedicalDiagnosticId()));

            // Validamos que el nuevo diagnostico no sea el mismo del UIConfig
            if (medicalDiagnostic.getId().equals(uiConfig.getMedicalDiagnostic().getId())) {
                return ApiResponse.error("El diagnóstico médico del estado UI no puede ser el mismo que el de la configuración UI, No podemos clasificar un diagnostico con su mismo diagnostico" );
            }

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
