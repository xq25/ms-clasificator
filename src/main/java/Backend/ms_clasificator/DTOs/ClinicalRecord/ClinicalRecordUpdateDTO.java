package Backend.ms_clasificator.DTOs.ClinicalRecord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
// Solo se permite actualizar directamente la fecha de visita, el resto esta asociado a una funcionalidad especifica.
public class ClinicalRecordUpdateDTO{
    @NotNull(message = "El motivo de la consulta no puede ser nulo")
    @NotBlank(message = "El motivo de la consulta no puede estar vacío")
    private String chiefComplaint;

    @NotNull
    private Date visitDate;
}

