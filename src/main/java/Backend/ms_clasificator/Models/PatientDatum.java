package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDatum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;

    // Cargamos la informacion del dato primitivo para mostrarla
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primitive_datum_id", nullable = false)
    private PrimitiveDatum primitiveDatum;

    // No vamos a cargar toda la informacion de la historia clinica,
    // ya que normalmente se consulta el patientdatums atraves de ella.
    // por lo cual tendriamos informacion repetida y poco util.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_record_id", nullable = false)
    private ClinicalRecord clinicalRecord;
}
