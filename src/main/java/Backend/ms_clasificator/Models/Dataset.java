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

    // Cargamos el diagnostico sobre el cual se genera el dataset de clasificacion.
    // Para generar un dataset debemos especificar que diagnostico vamos a clasificar
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_diagnostic_id", nullable = false)
    private MedicalDiagnostic medicalDiagnostic;

    // No necesitamos cargar el cada datasetCategory.
    // Si se elimina el datase, se deben eliminar toda su configuracion interna
    @JsonIgnore
    @OneToMany(mappedBy = "uiConfig", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DatasetCategory> datasetCategories;

    // Podemos primero generar el dataset y despues asignarle un area de evaluacion
    @OneToOne(mappedBy = "dataset", optional = true)
    private EvaluationArea evaluationArea;

}
