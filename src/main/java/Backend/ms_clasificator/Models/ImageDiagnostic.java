package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.query.common.FetchClauseType;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "medical_img_id", "medical_diagnostic_id"}))
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_diagnostic_id", nullable = false)
    private MedicalDiagnostic medicalDiagnostic;
    @JsonProperty("medicalDiagnosticId")
    public Integer getMedicalDiagnosticId() {
        return medicalDiagnostic != null ? medicalDiagnostic.getId() : null;
    }


    @Column(name = "diagnostic_date", nullable = false)
    private LocalDateTime diagnosticDate;
}