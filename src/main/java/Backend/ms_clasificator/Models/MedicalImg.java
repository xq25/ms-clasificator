package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "medical_img")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImg {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String url;

    // Relations ---
    /**Relacion con area de evaluacion, una imagen medica esta asociada directamente a un area de evaluacion*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_area_id", nullable = false)
    private EvaluationArea evaluationArea;

    /**Relacion con Diagnostico, una imagen puede tener varios|muchos diagnosticos*/
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "medicalImg_diagnostic",
            joinColumns = @JoinColumn(name = "medicalImg_id"),
            inverseJoinColumns = @JoinColumn(name = "diagnostic_id")
    )
    private List<Diagnostic> diagnostics;

    /**Relacion con tabla intermedia <diagnostico de imagen medica>*/
    @OneToMany(mappedBy = "medicalImg", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImgDiagnostic> imgDiagnostics;

}
