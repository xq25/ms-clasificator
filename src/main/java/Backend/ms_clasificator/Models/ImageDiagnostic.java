package Backend.ms_clasificator.Models;

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
// No puede existir dos diagnosticos de un mismo medico a una misma imagen
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "medical_img_id", "medical_diagnostic_id"}))
public class ImageDiagnostic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_img_id", nullable = false)
    private MedicalImg medicalImg;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_diagnostic_id", nullable = false)
    private MedicalDiagnostic medicalDiagnostic;

    @Column(name = "diagnostic_date", nullable = false)
    private LocalDateTime diagnosticDate;


}
