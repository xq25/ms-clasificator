package backend.msclasificator.clinicalrecord.application;

import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.clinicalrecord.api.dto.PatientDatumCreateDTO;
import backend.msclasificator.clinicalrecord.api.dto.PatientDatumResponseDTO;
import backend.msclasificator.clinicalrecord.api.dto.PatientDatumSummaryDTO;
import backend.msclasificator.clinicalrecord.api.dto.PatientDatumUpdateDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.clinicalrecord.api.mapper.PatientDatumMapper;
import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
import backend.msclasificator.clinicalrecord.domain.PatientDatum;
import backend.msclasificator.clinicalrecord.domain.PrimitiveDatum;
import backend.msclasificator.clinicalrecord.application.port.out.ClinicalRecordRepository;
import backend.msclasificator.clinicalrecord.application.port.out.PatientDatumRepository;
import backend.msclasificator.clinicalrecord.application.port.out.PrimitiveDatumRepository;
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
	public ApiResponse<PagedResponse<PatientDatumSummaryDTO>> findAll(PageRequestDTO pageRequest) {
		PageResult<PatientDatumSummaryDTO> page = patientDatumRepository.findAll(pageRequest.toPageQuery())
				.map(patientDatumMapper::toSummaryDTO);

		return ApiResponse.success(
				PagedResponse.<PatientDatumSummaryDTO>builder()
						.content(page.content())
						.page(page.page())
						.size(page.size())
						.totalElements(page.totalElements())
						.totalPages(page.totalPages())
						.last(page.last())
						.build(),
				"Patient Datum obtenidos exitosamente"
		);
	}

	@Transactional(readOnly = true)
	public ApiResponse<Long> count() {
		return ApiResponse.success(patientDatumRepository.count(), "Total de Patient Datum");
	}

	@Transactional(readOnly = true)
	public ApiResponse<PatientDatumResponseDTO> findById(Integer id) {
		try {
			PatientDatumResponseDTO patientDatum = patientDatumRepository.findById(id)
					.map(patientDatumMapper::toResponseDTO)
					.orElseThrow(() -> new IllegalArgumentException("Patient Datum no encontrado con ID: " + id));


			return ApiResponse.success(patientDatum, "Patient Datum encontrado exitosamente");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		}
	}

	// Funcion principal del flujo para ver la informacion de un clinical Record
	@Transactional(readOnly = true)
	public ApiResponse<List<PatientDatumResponseDTO>> findByClinicalRecordId(Integer clinicalRecordId) {
		try {
			if (!this.clinicalRecordRepository.existsById(clinicalRecordId)){
				return ApiResponse.error("No se encontre un clinicalRecord con id : " + clinicalRecordId);
			}

			List<PatientDatumResponseDTO>patientDatums = this.patientDatumRepository
					.findByClinicalRecordId(clinicalRecordId)
					.stream()
					.map(patientDatumMapper::toResponseDTO)
					.toList();

			if (patientDatums.isEmpty()) {
				return ApiResponse.success(patientDatums, "No se encontraron datos del paciente para esta visita medica: " + clinicalRecordId);
			}

			return ApiResponse.success(patientDatums, "Patient Datum encontrados exitosamente para el Clinical Record");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public ApiResponse<List<PatientDatumSummaryDTO>> findByPrimitiveDatumId(Integer primitiveDatumId) {
		try {
			if(!this.primitiveDatumRepository.existsById(primitiveDatumId)){
				return ApiResponse.error("No se encontre un primitiveDatum con id : " + primitiveDatumId);
			}

			List<PatientDatumSummaryDTO> patientDatums = this.patientDatumRepository
					.findByPrimitiveDatumId(primitiveDatumId)
					.stream()
					.map(patientDatumMapper::toSummaryDTO)
					.toList();

			if (patientDatums.isEmpty()) {
				return ApiResponse.success(patientDatums, "No se encontraron Patient Datum para el Primitive Datum con ID: " + primitiveDatumId);
			}

			return ApiResponse.success(patientDatums, "Patient Datum encontrados exitosamente para el Primitive Datum");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		}
	}

	public ApiResponse<PatientDatumResponseDTO> create(PatientDatumCreateDTO dto) {
		try {
			if (dto == null) {
				return ApiResponse.error("El DTO no puede ser nulo");
			}

			ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(dto.getClinicalRecordId())
					.orElseThrow(() -> new IllegalArgumentException("Clinical Record no encontrado con ID: " + dto.getClinicalRecordId()));

			PrimitiveDatum primitiveDatum = primitiveDatumRepository.findById(dto.getPrimitiveDatumId())
					.orElseThrow(() -> new IllegalArgumentException("Primitive Datum no encontrado con ID: " + dto.getPrimitiveDatumId()));

			// Validamos que el valor del dato corresponda con el tipo de dato primitivo.
			primitiveDatum.getType().validate(dto.getDescription());

			PatientDatum patientDatum = patientDatumMapper.toEntity(dto);
			patientDatum.setClinicalRecord(clinicalRecord);
			patientDatum.setPrimitiveDatum(primitiveDatum);

			PatientDatum saved = patientDatumRepository.save(patientDatum);
			return ApiResponse.success(patientDatumMapper.toResponseDTO(saved), "Patient Datum creado exitosamente");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
		}
	}

	@Transactional
	public ApiResponse<PatientDatumResponseDTO> update(Integer id, PatientDatumUpdateDTO dto) {
		try {
			if (dto == null) {
				return ApiResponse.error("El DTO no puede ser nulo");
			}

			PatientDatum existing = patientDatumRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Patient Datum no encontrado con ID: " + id));

			// Misma validación que create (bug #13): el valor debe ser compatible con el tipo del dato primitivo
			existing.getPrimitiveDatum().getType().validate(dto.getDescription());

			// Solo actualizamos la descripcion, la asociacion entre las dos entidades, solo puede ser generada y eliminada.
			existing.setDescription(dto.getDescription());

			return ApiResponse.success(patientDatumMapper.toResponseDTO(patientDatumRepository.save(existing)), "Patient Datum actualizado exitosamente");
		} catch (IllegalArgumentException ex) {
			return ApiResponse.error(ex.getMessage());
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
		}
	}

}
