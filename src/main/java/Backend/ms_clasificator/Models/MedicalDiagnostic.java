package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDiagnostic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Codigo que sigue el formato CIE-10 (Clasificación Internacional de Enfermedades) para estandarizar los diagnósticos médicos
    @Column(name = "diagnosticCode", nullable = false, unique = true)
    private String diagnosticCode;

    // Posibilidad de agregar informacion textual especifica del dianostico, para no depender solo del codigo
    @Column(name = "diagnosticName")
    private String diagnosticName;

    // Relación al diagnostico padre (self-referencing) - nullable
    // Foreing key del diagnostico padre, si lo hay
    @JsonIgnore // Para evitar problemas de serialización y referencias circulares al convertir a JSON
    @ManyToOne()
    @JoinColumn(name = "parent_diagnostic_id", nullable = true)
    private MedicalDiagnostic parentDiagnostic;
    public Integer getParentDiagnosticId() {
        return parentDiagnostic != null ? parentDiagnostic.getId() : null;
    }



}
