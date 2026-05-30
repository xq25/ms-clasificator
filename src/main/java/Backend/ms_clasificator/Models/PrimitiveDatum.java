package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
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

    @JsonIgnore
    @OneToMany(mappedBy = "primitiveDatum", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PatientDatum> patientDatums;

}
