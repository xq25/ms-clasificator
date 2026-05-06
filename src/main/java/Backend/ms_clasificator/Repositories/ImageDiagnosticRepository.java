package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.ImageDiagnostic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageDiagnosticRepository extends JpaRepository<ImageDiagnostic, Integer> {
}
