package backend.msclasificator.diagnosiscatalog.domain;

import backend.msclasificator.clinicalrecord.domain.Diagnosis;
import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import backend.msclasificator.dataset.domain.Dataset;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
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
    private MedicalDiagnostic parentDiagnostic;

    // Sin cascada (ADR-013): un diagnóstico se retira, nunca se borra, y lo histórico que lo referencia se conserva
    @JsonIgnore
    @OneToMany(mappedBy = "medicalDiagnostic")
    private List<Diagnosis> diagnosisList;

    @JsonIgnore
    @OneToMany(mappedBy = "medicalDiagnostic")
    private List<Dataset> datasets;

    @JsonIgnore
    @OneToMany(mappedBy = "medicalDiagnostic")
    private List<CategoryDiagnostic> diagnosticCategoryDatasets;

    // Soft delete (ADR-013, mismo patrón que ADR-011): `active` para filtrar y validar;
    // `deletedAt` registra cuándo se retiró. Solo cambian juntos, a través de retire().
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void retire() {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
    }

    @PrePersist
    void prePersist() {
        if (active == null) {
            active = true;
        }
    }
}
