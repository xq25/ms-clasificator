package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.PrimitiveDatum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrimitiveDatumRepository extends JpaRepository<PrimitiveDatum, Integer> {

	PrimitiveDatum findByName(String name);

	PrimitiveDatum findByNameIgnoreCase(String name);
}
