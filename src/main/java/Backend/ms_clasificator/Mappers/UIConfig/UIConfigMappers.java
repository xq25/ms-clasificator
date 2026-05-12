package Backend.ms_clasificator.Mappers.UIConfig;

import Backend.ms_clasificator.DTOs.UIConfig.CreateUIConfigDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.UIConfig;
import org.springframework.stereotype.Component;

@Component
public class UIConfigMappers implements Mapper<UIConfig, CreateUIConfigDTO> {

    @Override
    public UIConfig toEntity(CreateUIConfigDTO createUIConfigDTO) {
        if (createUIConfigDTO == null) {
            return null;
        }

        return UIConfig.builder()
                // Las relaciones se asignan en el Service
                .build();
    }

    @Override
    public CreateUIConfigDTO toDTO(UIConfig uiConfig) {
        if (uiConfig == null) {
            return null;
        }

        return CreateUIConfigDTO.builder()
                .medicalDiagnosticId(uiConfig.getMedicalDiagnostic() != null ? uiConfig.getMedicalDiagnostic().getId() : null)
                .build();
    }
}
