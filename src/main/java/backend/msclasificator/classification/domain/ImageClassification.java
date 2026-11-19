package backend.msclasificator.classification.domain;

import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.medicalimage.domain.MedicalImage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image_diagnostic", uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "medical_img_id"}))
public class ImageClassification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false, updatable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_img_id", nullable = false, updatable = false)
    private MedicalImage medicalImg;

    @Column(name = "diagnostic_date", nullable = false)
    private LocalDateTime diagnosticDate;

    // Solo lo colocamos por el cascade
    @JsonIgnore
    @OneToMany(mappedBy = "imageDiagnostic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassificationDiagnosis> imageDoctorDiagnostics;
}