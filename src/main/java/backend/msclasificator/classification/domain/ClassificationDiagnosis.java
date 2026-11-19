package backend.msclasificator.classification.domain;

import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;

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
@Table(name = "image_doctor_diagnostics", uniqueConstraints = @UniqueConstraint(columnNames = {"image_diagnostic_id", "medical_diagnostic_id"}))
// No puede existir dos veces el mismo diagnostico medico en un mismo diagnostico de imagen dado por un medico a una imagen especifica
public class ClassificationDiagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_diagnostic_id", nullable = false, updatable = false)
    private ImageClassification imageDiagnostic;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_diagnostic_id", nullable = false, updatable = false)
    private MedicalDiagnostic medicalDiagnostic;
}
