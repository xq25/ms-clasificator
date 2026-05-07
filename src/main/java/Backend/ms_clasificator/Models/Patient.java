package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document", nullable = false, unique = true)
    private String document;

    @Column(name = "years", nullable = false)
    private Integer years;

    @Column(name="user_id", nullable = false, unique = true)
    private String userId;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<MedicalImg> medicalImages;

}
