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

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrimitiveType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrimitiveUnit unit;

    // Lo colocamos solo para el cascade
    @JsonIgnore
    @OneToMany(mappedBy = "primitiveDatum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientDatum> patientDatumList;

}
