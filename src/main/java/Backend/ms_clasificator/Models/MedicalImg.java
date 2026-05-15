package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "evaluation_area_id", nullable = false)
    private Integer evaluationAreaId;

    @Column(name = "patient_id")
    private Integer patientId;

    // Relación con los diagnósticos de imagen asociados
    // Si se intenta eliminar una imagen diagnosticada, no lo debemos permitir
    @JsonIgnore
    @OneToMany(mappedBy = "medicalImg", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<ImageDiagnostic> imageDiagnostics;
}
