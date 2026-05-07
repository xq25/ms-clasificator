package Backend.ms_clasificator.Models;

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
public class EvaluationArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Normalmente usado en hospitales para diferenciar entre areas de evaluacion(No estandarizado)
    @Column(name = "codeArea", nullable = false)
    private String codeArea;

    @Column(name = "name", unique = true, nullable = false, updatable = true)
    private String name;
}
