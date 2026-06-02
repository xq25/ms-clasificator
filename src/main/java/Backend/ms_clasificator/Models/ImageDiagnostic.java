package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false, updatable = false)
    private Doctor doctor;
    @JsonProperty("doctorId")
    public Integer getDoctorId() {
        return doctor != null ? doctor.getId() : null;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_img_id", nullable = false, updatable = false)
    private MedicalImg medicalImg;
    @JsonProperty("medicalImgId")
    public Integer getMedicalImgId() {
        return medicalImg != null ? medicalImg.getId() : null;
    }

    // No necesitamos cargar todos los diagnosticos que se le dieron a esta imagen por este medico.
    // Si se elimina el diagnostico que dio el medico, se debe eliminar toda la informacion que a este compone
    @JsonIgnore
    @OneToMany(mappedBy = "imageDiagnostic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ImageDoctorDiagnostics> imageDoctorDiagnostics;

    @Column(name = "diagnostic_date", nullable = false)
    private LocalDateTime diagnosticDate;
}