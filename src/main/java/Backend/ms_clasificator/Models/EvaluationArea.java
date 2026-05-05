package Backend.ms_clasificator.Models;

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
    @Column(name = "codeArea", nullable = false)
    private String codeArea;

    @Column(name = "name", unique = true, nullable = false, updatable = true)
    private String name;

    @OneToMany(mappedBy = "evaluationArea", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DoctorArea> doctorAreas;

    @OneToMany(mappedBy = "evaluationArea", fetch = FetchType.LAZY)
    private List<MedicalImg> medicalImages;

}
