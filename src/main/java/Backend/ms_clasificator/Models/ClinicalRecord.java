package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "visitDate", nullable = false)
    private Date visitDate;

    // No se puede eliminar una visita medica si tiene imagenes asociadas
    @OneToMany(mappedBy = "clinicalRecord", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<MedicalImg> medicalImages;








}
