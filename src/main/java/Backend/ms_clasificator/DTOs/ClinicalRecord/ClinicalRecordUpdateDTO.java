package Backend.ms_clasificator.DTOs.ClinicalRecord;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
// Solo se permite actualizar directamente la fecha de visita, el resto esta asociado a una funcionalidad especifica.
public class ClinicalRecordUpdateDTO{
    @NotNull
    private Date visitDate;
}

