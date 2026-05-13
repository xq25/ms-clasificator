package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @ManyToOne()
    @JoinColumn(name = "parent_diagnostic_id", nullable = true)
    @JsonIgnore // Para evitar problemas de serialización y referencias circulares al convertir a JSON
    private MedicalDiagnostic parentDiagnostic;
    public Integer getParentDiagnosticId() {
        return parentDiagnostic != null ? parentDiagnostic.getId() : null;
    }

    // Relación a los sub-diagnosticos - nullable
    // Buscamos en esta misma tabla en base al parentDiagnostic, este es el campo por el cual podemos saber cuantos diagnosticos pertenecen a un diagnostico padre
    @OneToMany(mappedBy = "parentDiagnostic", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore// Para evitar problemas de serialización y referencias circulares al convertir a JSON
    private List<MedicalDiagnostic> subDiagnostics = new ArrayList<>();

    // Relacion con los puntos de evaluacion asociados a este diagnostico
    @JsonIgnore // Para evitar problemas de serialización y referencias circulares al convertir a JSON
    //Si se elimina un medicalDiagnostic se deben eliminar sus composiciones
    @OneToMany(mappedBy = "medicalDiagnostic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UIState> uiStates;

    // Relación a losUIConfig asociados a este diagnóstico o enfermedad
    @JsonIgnore // Para evitar problemas de serialización y referencias circulares al convertir a JSON
    @OneToMany(mappedBy = "medicalDiagnostic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    //Si se elimina un medicalDiagnostic se deben eliminar sus composiciones
    private List<UIConfig> uiConfigs;



}
