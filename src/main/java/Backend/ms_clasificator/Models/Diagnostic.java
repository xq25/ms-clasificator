package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "diagnostic")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnostic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "medical_code", nullable = false)
    private String medicalCode;

    @ManyToMany(mappedBy = "diagnostics", fetch = FetchType.LAZY)
    private List<Imagenologia> imagenologias;
}