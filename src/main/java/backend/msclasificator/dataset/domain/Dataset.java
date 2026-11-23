package backend.msclasificator.dataset.domain;

import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import backend.msclasificator.medicalimage.domain.MedicalImageType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Sin UNIQUE en BD: el nombre es único solo entre datasets activos (ADR-011)
    @Column(name = "name", nullable = false)
    private String name;

    // Cargamos el diagnostico sobre el cual se genera el dataset de clasificacion.
    // Para generar un dataset debemos especificar que diagnostico vamos a clasificar
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_diagnostic_id", nullable = false)
    private MedicalDiagnostic medicalDiagnostic;

    // Carga el tipo de imagen que se va a clasificar. ManyToOne: un tipo puede tener datasets retirados (ADR-011)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_image_type_id", nullable = false)
    private MedicalImageType medicalImageType;

    // Solo lo colocamos para el cascade
    @JsonIgnore
    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DatasetCategory> datasetCategories;

    // Soft delete (ADR-011): `active` para filtrar y validar; `deletedAt` registra cuándo se retiró.
    // Solo cambian juntos, a través de retire().
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
