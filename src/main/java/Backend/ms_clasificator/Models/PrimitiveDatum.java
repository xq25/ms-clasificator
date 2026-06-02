package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PrimitiveDatum extends SystemDatum{

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrimitiveType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrimitiveUnit unit;

    // Si se elimina el dato primitivo se deben eliminaar todos los datos de un historial clinico de un paciente que esten relacionados con este.
    @JsonIgnore
    @OneToMany(mappedBy = "primitiveDatum", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PatientDatum> patientDatums;

}
