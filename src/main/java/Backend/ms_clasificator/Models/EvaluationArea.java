package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class EvaluationArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String codeArea;

    @Column(name = "name", unique = true, nullable = false, updatable = true)
    private String name;

    @OneToMany(mappedBy = "evaluationArea", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DoctorArea> doctorAreas;

    @OneToMany(mappedBy = "evaluationArea", fetch = FetchType.LAZY)
    private List<MedicalImg> medicalImages;

}
