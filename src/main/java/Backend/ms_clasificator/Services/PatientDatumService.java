package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.PatientDatum.PatientDatumCreateDTO;
import Backend.ms_clasificator.DTOs.PatientDatum.PatientDatumResponseDTO;
import Backend.ms_clasificator.DTOs.PatientDatum.PatientDatumUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.PatientDatumMappers.PatientDatumMapper;
import Backend.ms_clasificator.Models.ClinicalRecord;
import Backend.ms_clasificator.Models.PatientDatum;
import Backend.ms_clasificator.Models.PrimitiveDatum;
import Backend.ms_clasificator.Repositories.ClinicalRecordRepository;
import Backend.ms_clasificator.Repositories.PatientDatumRepository;
import Backend.ms_clasificator.Repositories.PrimitiveDatumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientDatumService {

	@Autowired
	private PatientDatumRepository patientDatumRepository;

	@Autowired
	private ClinicalRecordRepository clinicalRecordRepository;

	@Autowired
	private PrimitiveDatumRepository primitiveDatumRepository;

	@Autowired
	private PatientDatumMapper patientDatumMapper;

	@Transactional(readOnly = true)
	public ApiResponse<List<PatientDatumResponseDTO>> findAll() {
		try {
			List<PatientDatumResponseDTO> response = patientDatumRepository.findAll()
					.stream()
					.map(patientDatumMapper::toResponseDTO)
					.toList();
			return ApiResponse.success(response, "Patient Datum obtenidos exitosamente");
		} catch (Exception ex) {
			return ApiResponse.error("Error al listar Patient Datum: " + ex.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public ApiResponse<PatientDatumResponseDTO> findById(Integer id) {
		try {
			PatientDatum patientDatum = patientDatumRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Patient Datum no encontrado con ID: " + id));

			return ApiResponse.success(patientDatumMapper.toResponseDTO(patientDatum), "Patient Datum encontrado exitosamente");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.error("Error al buscar Patient Datum: " + ex.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public ApiResponse<List<PatientDatumResponseDTO>> findByClinicalRecordId(Integer clinicalRecordId) {
		try {
			ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(clinicalRecordId)
					.orElseThrow(() -> new IllegalArgumentException("Clinical Record no encontrado con ID: " + clinicalRecordId));

			List<PatientDatum> patientDatums = patientDatumRepository.findByClinicalRecord_Id(clinicalRecord.getId());
			List<PatientDatumResponseDTO> response = patientDatums.stream().map(patientDatumMapper::toResponseDTO).toList();
			if (patientDatums.isEmpty()) {
				return ApiResponse.success(response, "No se encontraron datos del paciente para esta visita medica: " + clinicalRecordId);
			}

			return ApiResponse.success(response, "Patient Datum encontrados exitosamente para el Clinical Record");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.error("Error al buscar Patient Datum por Clinical Record: " + ex.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public ApiResponse<List<PatientDatumResponseDTO>> findByPrimitiveDatumId(Integer primitiveDatumId) {
		try {
			PrimitiveDatum primitiveDatum = primitiveDatumRepository.findById(primitiveDatumId)
					.orElseThrow(() -> new IllegalArgumentException("Primitive Datum no encontrado con ID: " + primitiveDatumId));

			List<PatientDatum> patientDatums = patientDatumRepository.findByPrimitiveDatum_Id(primitiveDatum.getId());
			List<PatientDatumResponseDTO> response = patientDatums.stream().map(patientDatumMapper::toResponseDTO).toList();
			if (patientDatums.isEmpty()) {
				return ApiResponse.success(response, "No se encontraron Patient Datum para el Primitive Datum con ID: " + primitiveDatumId);
			}

			return ApiResponse.success(response, "Patient Datum encontrados exitosamente para el Primitive Datum");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.error("Error al buscar Patient Datum por Primitive Datum: " + ex.getMessage());
		}
	}

	public ApiResponse<PatientDatumResponseDTO> create(PatientDatumCreateDTO dto) {
		try {
			if (dto == null) {
				return ApiResponse.error("El DTO no puede ser nulo");
			}

			if (dto.getClinicalRecordId() == null) {
				return ApiResponse.error("El clinicalRecord es obligatorio");
			}

			if (dto.getPrimitiveDatumId() == null) {
				return ApiResponse.error("El primitiveDatum es obligatorio");
			}

			ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(dto.getClinicalRecordId())
					.orElseThrow(() -> new IllegalArgumentException("Clinical Record no encontrado con ID: " + dto.getClinicalRecordId()));

			PrimitiveDatum primitiveDatum = primitiveDatumRepository.findById(dto.getPrimitiveDatumId())
					.orElseThrow(() -> new IllegalArgumentException("Primitive Datum no encontrado con ID: " + dto.getPrimitiveDatumId()));

			PatientDatum patientDatum = patientDatumMapper.toEntity(dto);
			patientDatum.setClinicalRecord(clinicalRecord);
			patientDatum.setPrimitiveDatum(primitiveDatum);

			PatientDatum saved = patientDatumRepository.save(patientDatum);
			return ApiResponse.success(patientDatumMapper.toResponseDTO(saved), "Patient Datum creado exitosamente");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.error("Error al crear Patient Datum: " + ex.getMessage());
		}
	}

	public ApiResponse<PatientDatumResponseDTO> update(Integer id, PatientDatumUpdateDTO dto) {
		try {
			if (dto == null) {
				return ApiResponse.error("El DTO no puede ser nulo");
			}

			PatientDatum existing = patientDatumRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Patient Datum no encontrado con ID: " + id));

			// Solo actualizamos la descripcion, la asociacion entre las dos entidades, solo puede ser generada y eliminada.
			if (dto.getDescription() != null) {
				existing.setDescription(dto.getDescription());
			}

			PatientDatum updated = patientDatumRepository.save(existing);
			return ApiResponse.success(patientDatumMapper.toResponseDTO(updated), "Patient Datum actualizado exitosamente");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.error("Error al actualizar Patient Datum: " + ex.getMessage());
		}
	}

	@Transactional
	public ApiResponse<Void> delete(Integer id) {
		try {
			PatientDatum patientDatum = patientDatumRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Patient Datum no encontrado con ID: " + id));

			patientDatumRepository.delete(patientDatum);
			return ApiResponse.success("Patient Datum eliminado exitosamente");
		} catch (DataIntegrityViolationException ex) {
			return ApiResponse.error("Violacion a integridad de la base de datos: " + ex.getMessage());
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		} catch (Exception ex) {
			return ApiResponse.error("Error al eliminar Patient Datum: " + ex.getMessage());
		}
	}

}
