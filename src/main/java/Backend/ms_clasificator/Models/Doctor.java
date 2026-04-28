package Backend.ms_clasificator.Models;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctor")
@Data
@NoArgsConstructor
public class Doctor {
    // Identificador principal del medico
    @Id
    @Column(nullable = false, unique = true)
    private String code;

    // Referencia externa al microservicio de seguridad (no hay relación JPA)
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    // Area de evaluacion a la que pertenece
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_area_id", nullable = false)
    private EvaluationArea evaluationArea;

    // Relacion de diligenciamiento con respecto aa las calificaciones o grado de evaluaciom de un medico dado para una imagen especifica
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImgDiagnostic> imgDiagnostics;

    public Doctor(String code, UUID userId, EvaluationArea evaluationArea) {
        this.code = code;
        this.userId = userId;
        this.evaluationArea = evaluationArea;
        this.imgDiagnostics = null;
    }
}