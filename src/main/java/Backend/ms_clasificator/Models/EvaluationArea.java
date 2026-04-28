package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "evaluation_area")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "evaluationArea", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Doctor> doctors;

    @OneToMany(mappedBy = "evaluationArea", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Imagenologia> imagenologias;
}