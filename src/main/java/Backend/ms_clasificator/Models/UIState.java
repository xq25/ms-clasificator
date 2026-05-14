package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UIState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    @JoinColumn(name = "ui_config_id", nullable = false, updatable = false)
    private UIConfig uiConfig;
    @JsonProperty("uiConfigId")
    public Integer getUiConfigId() {
        return uiConfig != null ? uiConfig.getId() : null;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_diagnostic_id", nullable = false)
    private MedicalDiagnostic medicalDiagnostic;
    @JsonProperty("medicalDiagnosticId")
    public Integer getMedicalDiagnosticId() {
        return medicalDiagnostic != null
                ? medicalDiagnostic.getId()
                : null;
    }

}

