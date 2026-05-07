package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @ManyToOne
    @JoinColumn(name = "parent_diagnostic_id", nullable = true)
    private MedicalDiagnostic parentDiagnostic;

    // Relación a los sub-diagnosticos - nullable
    // Buscamos en esta misma tabla en base al parentDiagnostic, este es el campo por el cul podemos saber cuantos diagnosticos pertenecen a un diagnostico padre
    @OneToMany(mappedBy = "parentDiagnostic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MedicalDiagnostic> subDiagnostics;

    // Relacion con los puntos de evaluacion asociados a este diagnostico
    @OneToMany(mappedBy = "medicalDiagnostic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UIState> uiStates;

    // Relación a losUIConfig asociados a este diagnóstico o enfermedad
    @OneToMany(mappedBy = "medicalDiagnostic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UIConfig> uiConfigs;



}
