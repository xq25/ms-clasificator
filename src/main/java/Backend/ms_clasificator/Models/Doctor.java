package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "user_id", unique = true, nullable = false, updatable = false)
    private String userId;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DoctorArea> doctorAreas;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<ImageDiagnostic> imageDiagnostics;
}