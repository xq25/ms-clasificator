package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "evaluation_area_id", nullable = true)
    private EvaluationArea evaluationArea;

}
