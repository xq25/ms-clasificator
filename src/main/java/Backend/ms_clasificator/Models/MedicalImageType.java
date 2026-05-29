package Backend.ms_clasificator.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @JsonIgnore // No cargamos todas las imagenes, no se puede eliminar un tipo de imagen si hay imagenes asociadas a el.
    @OneToMany(mappedBy = "medicalImageType", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<MedicalImg> medicalImgs;

    // Podemos generar el tipo de imagenes y despues asociarlas a un area de evaluacion
    @ManyToOne(fetch = FetchType.LAZY)
    private EvaluationArea evaluationArea;
}
