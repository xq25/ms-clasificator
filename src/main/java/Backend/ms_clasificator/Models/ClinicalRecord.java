package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // Se permite eliminar la historia clinica, pero las imagenes persisten. Se aplica desde el service.
    @JsonIgnore
    @OneToMany(mappedBy = "clinicalRecord", fetch = FetchType.LAZY)
    private List<MedicalImg> medicalImages;

    // Se eliminan todos los datos primitios dentro de esa visita medica.
    @JsonIgnore
    @OneToMany(mappedBy = "clinicalRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PatientDatum> patientDatums;

    // Es obliatorio tener un paciente a la hora de generar la historia clinica
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Patient patient;

    // Si se elimina una visita medica, tambien debemos de eliminar todos los diagnosticos que se le hicieron a ese paciente durante ella.
    @JsonIgnore
    @OneToMany(mappedBy = "clinicalRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Diagnosis> diagnoses;

}
