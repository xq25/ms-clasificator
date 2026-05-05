package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "DoctorArea")
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Apartado para coherencia de datos evitando duplicados entre doctores y areas. No pueden haber dos asignaciones de un mismo doctor a un mismo area.
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "evaluation_area_id"}))
public class DoctorArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // fetch.lazy -> Indica que tipo de carga hacemos con los datos de nuestro sistema, si nos traemos todas las instancias siempre(eager) o !solo cuando se requiere! (lazy)
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Optional = false, ya que no puede haber relaciones a medias o con null, todo lo que este en esta tabla intermedia debe ser por una relacion consistente
    @JoinColumn( name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn( name = "evaluation_area_id", nullable = false)
    private EvaluationArea evaluationArea;

}
