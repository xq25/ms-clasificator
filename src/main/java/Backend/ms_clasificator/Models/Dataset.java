package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    @Column(name = "name", unique = true)
    private String name;

    // Cargamos el diagnostico sobre el cual se genera el dataset de clasificacion.
    // Para generar un dataset debemos especificar que diagnostico vamos a clasificar
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_diagnostic_id", nullable = false)
    private MedicalDiagnostic medicalDiagnostic;

    // Carga el tipo de imagen que se va a clasificar.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_image_type_id", nullable = false)
    private MedicalImageType medicalImageType;

    // Solo lo colocamos para el cascade
    @JsonIgnore
    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DatasetCategory> datasetCategories;

}
