package Backend.ms_clasificator.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Lazy;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticCategoryDataset {
    @Id
    @GeneratedValue
    private Integer id;

    // No cargamos el dataset al que oertence, ya que desde el es que se consultan estas entidades
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DatasetCategory datasetCategory;

    // Cargamos el diagnostico medico que tiene este state
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private MedicalDiagnostic medicalDiagnostic;

}
