package backend.msclasificator.medicalimage.domain;

import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.doctor.domain.EvaluationArea;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalImageType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Único solo entre tipos activos (ADR-013): lo valida el servicio, no la BD
    @Column(name = "name", nullable = false)
    private String name;

    // Podemos generar el tipo de imagenes y despues asociarlas a un area de evaluacion
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private EvaluationArea evaluationArea;

    // Varios datasets por tipo (uno activo + retirados, ADR-011); la unicidad del activo la valida el servicio.
    // Sin cascada (bug #23): los datasets nunca se borran físicamente. MedicalImageTypeService.delete
    // rechaza el borrado si el tipo tiene datasets, incluidos los retirados.
    @JsonIgnore
    @OneToMany(mappedBy = "medicalImageType")
    private List<Dataset> datasets;

    // Soft delete (ADR-013, mismo patrón que ADR-011): `active` para filtrar y validar;
    // `deletedAt` registra cuándo se retiró. Solo cambian juntos, a través de retire().
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Al retirarse deja de pertenecer a su área: el área solo da permiso para clasificar (ADR-013)
    public void retire() {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
        this.evaluationArea = null;
    }

    @PrePersist
    void prePersist() {
        if (active == null) {
            active = true;
        }
    }
}
