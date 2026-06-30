package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.PrimitiveDatum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PrimitiveDatumRepository extends JpaRepository<PrimitiveDatum, Integer> {

	PrimitiveDatum findByNameIgnoreCase(String name);

	@Query("SELECT COUNT(p) FROM PrimitiveDatum p")
	long countAll();
}
