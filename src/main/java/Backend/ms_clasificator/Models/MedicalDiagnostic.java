package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class MedicalDiagnostic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "diagnosticCode", nullable = false, unique = true)
    private String diagnosticCode;

    @Column(name = "diagnosticName")
    private String diagnosticName;

    @OneToMany(mappedBy = "medicalDiagnostic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageDiagnostic> imageDiagnostics;



}
