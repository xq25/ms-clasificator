package backend.msclasificator.dataset.domain;

import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "diagnostic_category_dataset")
public class CategoryDiagnostic {
    // Tabla y secuencia fijadas a los nombres previos al renombre de la entidad (ADR-003)
    @Id
    @GeneratedValue(generator = "category_diagnostic_seq")
    @SequenceGenerator(name = "category_diagnostic_seq", sequenceName = "DiagnosticCategoryDataset_SEQ")
    private Integer id;

    // No cargamos el dataset al que pertence, ya que desde el es que se consultan estas entidades
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DatasetCategory datasetCategory;

    // Cargamos el diagnostico medico que tiene este state
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private MedicalDiagnostic medicalDiagnostic;

    // Inicio de vigencia en la definición de la categoría (ADR-011)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
