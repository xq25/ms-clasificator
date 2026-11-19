package backend.msclasificator.clinicalrecord.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalRecordSummaryDTO {

    private Integer id;
    private String chiefComplaint;
    private Date visitDate;

}
