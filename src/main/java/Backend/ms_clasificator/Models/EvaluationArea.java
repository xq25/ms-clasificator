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
public class EvaluationArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Normalmente usado en hospitales para diferenciar entre areas de evaluacion(No estandarizado)
    @Column(name = "codeArea", unique = true, nullable = false)
    private String codeArea;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    // Solo lo agreamos para el cascade
    @JsonIgnore
    @OneToMany(mappedBy = "evaluationArea", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorArea> doctorAreas;

    // No es necesario cargar la configuracion(Dataset) para clasificar, que tiene asiganada esta area de evaluacion.
    @JsonIgnore
    @OneToOne(mappedBy = "evaluationArea", optional = true)
    private Dataset dataset;
}
