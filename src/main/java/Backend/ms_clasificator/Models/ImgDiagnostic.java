package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "img_diagnostic")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImgDiagnostic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_code", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imagenologia_id", nullable = false)
    private Imagenologia imagenologia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnostic_id", nullable = false)
    private Diagnostic diagnostic;
}