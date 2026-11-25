package backend.msclasificator.clinicalrecord.domain;

import backend.msclasificator.shared.domain.SystemDatum;

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

    @Column(name = "name", nullable = false, unique = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrimitiveType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrimitiveUnit unit;

    // Sin cascada (bug #15): borrar un dato primitivo no puede borrar valores clínicos de pacientes.
    // PrimitiveDatumService.delete rechaza el borrado si hay valores registrados.
    @JsonIgnore
    @OneToMany(mappedBy = "primitiveDatum")
    private List<PatientDatum> patientDatumList;

}
