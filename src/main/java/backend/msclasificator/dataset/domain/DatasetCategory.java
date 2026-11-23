package backend.msclasificator.dataset.domain;

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
public class DatasetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    private Integer numValue;

    // Cargamos el dataset al que pertenece para mostrar unicamnete le id para futuras consultas
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false, updatable = false)
    private Dataset dataset;

    // Solo lo colocamos por el cascade, no lo mostramos en el json
    @JsonIgnore
    @OneToMany(mappedBy = "datasetCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryDiagnostic> diagnosticCategoryDatasets;

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

