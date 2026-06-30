package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Pagination.PageRequestDTO;
import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumCreateDTO;
import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumResponseDTO;
import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.DTOs.Response.PagedResponse;
import org.springframework.data.domain.Page;
import Backend.ms_clasificator.Mappers.PrimitiveDatumMappers.PrimitiveDatumMapper;
import Backend.ms_clasificator.Models.PrimitiveDatum;
import Backend.ms_clasificator.Repositories.PrimitiveDatumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrimitiveDatumService {

	@Autowired
	private PrimitiveDatumRepository primitiveDatumRepository;

	@Autowired
	private PrimitiveDatumMapper primitiveDatumMapper;

	@Transactional(readOnly = true)
	public ApiResponse<PagedResponse<PrimitiveDatumResponseDTO>> findAll(PageRequestDTO pageRequest) {
		try {
			Page<PrimitiveDatumResponseDTO> page = primitiveDatumRepository.findAll(pageRequest.toPageable())
					.map(primitiveDatumMapper::toResponseDTO);

			return ApiResponse.success(
					PagedResponse.<PrimitiveDatumResponseDTO>builder()
							.content(page.getContent())
							.page(page.getNumber())
							.size(page.getSize())
							.totalElements(page.getTotalElements())
							.totalPages(page.getTotalPages())
							.last(page.isLast())
							.build(),
					"Datos primitivos obtenidos exitosamente"
			);
		} catch (Exception ex) {
			return ApiResponse.error("Error al listar datos primitivos: " + ex.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public ApiResponse<Long> count() {
		return ApiResponse.success(primitiveDatumRepository.countAll(), "Total de datos primitivos");
	}

	@Transactional(readOnly = true)
	public ApiResponse<PrimitiveDatumResponseDTO> findById(Integer id) {
		try {
			PrimitiveDatum primitiveDatum = primitiveDatumRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Dato primitivo no encontrado con ID: " + id));

			return ApiResponse.success(primitiveDatumMapper.toResponseDTO(primitiveDatum), "Dato primitivo encontrado exitosamente");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.error("Error al buscar dato primitivo: " + ex.getMessage());
		}
	}

	public ApiResponse<PrimitiveDatumResponseDTO> create(PrimitiveDatumCreateDTO dto) {
		try {
			if (dto == null) {
				return ApiResponse.error("El DTO no puede ser nulo");
			}

			// Validamos que no exista otro dato con este mismo nombre
			PrimitiveDatum existing = primitiveDatumRepository.findByNameIgnoreCase(dto.getName());
			if (existing != null) {
				return ApiResponse.error("Ya existe un dato primitivo con el nombre: " + dto.getName());
			}

			PrimitiveDatum primitiveDatum = primitiveDatumMapper.toEntity(dto);
			PrimitiveDatum saved = primitiveDatumRepository.save(primitiveDatum);
			return ApiResponse.success(primitiveDatumMapper.toResponseDTO(saved), "Dato primitivo creado exitosamente");
		} catch (DataIntegrityViolationException ex) {
			return ApiResponse.error("Violacion a integridad de la base de datos: " + ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.error("Error al crear dato primitivo: " + ex.getMessage());
		}
	}

	public ApiResponse<PrimitiveDatumResponseDTO> update(Integer id, PrimitiveDatumUpdateDTO dto) {
		try {
			if (dto == null) {
				return ApiResponse.error("El DTO no puede ser nulo");
			}

			PrimitiveDatum primitiveDatum = primitiveDatumRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Dato primitivo no encontrado con ID: " + id));

			// Validamos que no tenga el mismo nombre que otro dato primitivo
			PrimitiveDatum existing = primitiveDatumRepository.findByNameIgnoreCase(dto.getName());
			if (existing != null && !existing.getId().equals(id)) {
				return ApiResponse.error("Ya existe un dato primitivo con el nombre: " + dto.getName());
			}

			primitiveDatum.setName(dto.getName());
			primitiveDatum.setType(dto.getType());
			primitiveDatum.setUnit(dto.getUnit());

			PrimitiveDatum updated = primitiveDatumRepository.save(primitiveDatum);
			return ApiResponse.success(primitiveDatumMapper.toResponseDTO(updated), "Dato primitivo actualizado exitosamente");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		} catch (DataIntegrityViolationException ex) {
			return ApiResponse.error("Violacion a integridad de la base de datos: " + ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.error("Error al actualizar dato primitivo: " + ex.getMessage());
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ApiResponse<Void> delete(Integer id) {
		try {
			PrimitiveDatum primitiveDatum = primitiveDatumRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Dato primitivo no encontrado con ID: " + id));

			primitiveDatumRepository.delete(primitiveDatum);
			return ApiResponse.success("Dato primitivo eliminado exitosamente");
		} catch (DataIntegrityViolationException ex) {
			return ApiResponse.error("Violacion a integridad de la base de datos: " + ex.getMessage());
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.error("Error al eliminar dato primitivo: " + ex.getMessage());
		}
	}
}
