package Backend.ms_clasificator.Models;

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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "medical_img_id"}))
public class ImageDiagnostic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false, updatable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_img_id", nullable = false, updatable = false)
    private MedicalImg medicalImg;

    @Column(name = "diagnostic_date", nullable = false)
    private LocalDateTime diagnosticDate;

    // Solo lo colocamos por el cascade
    @JsonIgnore
    @OneToMany(mappedBy = "imageDiagnostic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageDoctorDiagnostics> imageDoctorDiagnostics;
}