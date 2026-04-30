package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cc", nullable = false, unique = true)
    private String cc;

    @Column(name = "years", nullable = false)
    private Integer years;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<MedicalImg> medicalImages;

}
