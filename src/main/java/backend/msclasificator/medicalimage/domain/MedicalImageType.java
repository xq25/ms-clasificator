package backend.msclasificator.medicalimage.domain;

import backend.msclasificator.dataset.domain.Dataset;
import backend.msclasificator.doctor.domain.EvaluationArea;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalImageType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    // Podemos generar el tipo de imagenes y despues asociarlas a un area de evaluacion
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private EvaluationArea evaluationArea;

    // Lo colocamos solo por el cascade. Un dataset no puede existir si se borra el tipo de imagen que iba a clasificar
    @JsonIgnore
    @OneToOne(mappedBy = "medicalImageType", cascade = CascadeType.ALL, orphanRemoval = true)
    private Dataset dataset;
}
