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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"image_diagnostic_id", "medical_diagnostic_id"}))
// No puede existir dos veces el mismo diagnostico medico en un mismo diagnostico de imagen dado por un medico a una imagen especifica
public class ImageDoctorDiagnostics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_diagnostic_id", nullable = false, updatable = false)
    private ImageDiagnostic imageDiagnostic;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_diagnostic_id", nullable = false, updatable = false)
    private MedicalDiagnostic medicalDiagnostic;
}
