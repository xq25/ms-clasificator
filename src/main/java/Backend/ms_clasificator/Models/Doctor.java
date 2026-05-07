package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", unique = true, nullable = false, updatable = false)
    private String code;

    @Column(name = "user_id", unique = true, nullable = false, updatable = false)
    private String userId;

    // Si se elimina el doctor, se deben eliminar las relaciones automaticamente, para no tener relaciones huerfanas
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DoctorArea> doctorAreas;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ImageDiagnostic> imageDiagnostics;

}
