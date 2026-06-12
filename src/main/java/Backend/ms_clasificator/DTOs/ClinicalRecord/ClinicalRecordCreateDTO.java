package Backend.ms_clasificator.DTOs.ClinicalRecord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalRecordCreateDTO {

    @NotNull(message = "El motivo de la consulta no puede ser nulo")
    @NotBlank(message = "El motivo de la consulta no puede estar vacío")
    private String chiefComplaint;


    @NotNull(message = "La fecha de la visita no puede ser nula")
    private Date visitDate;

    @NotNull(message = "El patientId no puede ser nulo")
    private Integer patientId;
}

