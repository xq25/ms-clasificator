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

    @Column(name = "chiefComplaint", nullable = false)
    private String chiefComplaint;

    @Column(name = "visitDate", nullable = false)
    private Date visitDate;

    // Es obliatorio tener un paciente a la hora de generar la historia clinica
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Patient patient;

    // Solo lo colocamos para el cascade
    @JsonIgnore
    @OneToMany(mappedBy = "clinicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientDatum> patientDatumList;

    // Solo lo agregamos para el cascade
    @JsonIgnore
    @OneToMany(mappedBy = "clinicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diagnosis> diagnosisList;

}
