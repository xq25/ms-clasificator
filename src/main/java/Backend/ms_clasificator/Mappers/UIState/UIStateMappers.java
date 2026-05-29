package Backend.ms_clasificator.Mappers.UIState;

import Backend.ms_clasificator.DTOs.UIState.CreateUIStateDTO;
import Backend.ms_clasificator.DTOs.UIState.UIStateResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.UIState;
import org.springframework.stereotype.Component;

@Component
public class UIStateMappers implements Mapper<UIState, CreateUIStateDTO, UIStateResponseDTO> {

    @Override
    public UIState toEntity(CreateUIStateDTO createUIStateDTO) {
        if (createUIStateDTO == null) {
            return null;
        }

        return UIState.builder()
                // Las relaciones se asignan en el Service
                .build();
    }

    @Override
    public CreateUIStateDTO toDTO(UIState uiState) {
        if (uiState == null) {
            return null;
        }

        return CreateUIStateDTO.builder()
                .uiConfigId(uiState.getUiConfig() != null ? uiState.getUiConfig().getId() : null)
                .medicalDiagnosticId(uiState.getMedicalDiagnostic() != null ? uiState.getMedicalDiagnostic().getId() : null)
                .build();
    }
    @Override
    public UIStateResponseDTO toResponseDTO(UIState uiState){
        if (uiState == null){
            return null;
        }
        return UIStateResponseDTO.builder()
                .id(uiState.getId())
                .uiConfigId(uiState.getUiConfig().getId())
                .medicalDiagnosticId(uiState.getMedicalDiagnosticId())
                .build();
    }
}
