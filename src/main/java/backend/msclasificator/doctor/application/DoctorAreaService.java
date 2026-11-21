package backend.msclasificator.doctor.application;

import backend.msclasificator.doctor.api.dto.DoctorAreaCreateDTO;
import backend.msclasificator.doctor.api.dto.DoctorAreaResponseDTO;
import backend.msclasificator.doctor.api.dto.DoctorAreaSummaryDTO;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.doctor.api.mapper.DoctorAreaMapper;
import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.doctor.domain.DoctorArea;
import backend.msclasificator.doctor.domain.EvaluationArea;
import backend.msclasificator.doctor.application.port.out.DoctorAreaRepository;
import backend.msclasificator.doctor.application.port.out.DoctorRepository;
import backend.msclasificator.doctor.application.port.out.EvaluationAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DoctorAreaService {

    @Autowired
    private DoctorAreaRepository doctorAreaRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;

    @Autowired
    private DoctorAreaMapper doctorAreaMapper;

    /**
     * Obtener todas las relaciones DoctorArea
     * @return Lista de todas las relaciones
     */
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<DoctorAreaSummaryDTO>> findAll(PageRequestDTO pageRequest) {
        PageResult<DoctorAreaSummaryDTO> page = doctorAreaRepository.findAll(pageRequest.toPageQuery())
                .map(doctorAreaMapper::toSummaryDTO);

        return ApiResponse.success(
                PagedResponse.<DoctorAreaSummaryDTO>builder()
                        .content(page.content())
                        .page(page.page())
                        .size(page.size())
                        .totalElements(page.totalElements())
                        .totalPages(page.totalPages())
                        .last(page.last())
                        .build(),
                "Relaciones DoctorArea obtenidas exitosamente"
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Long> count() {
        return ApiResponse.success(doctorAreaRepository.count(), "Total de relaciones DoctorArea");
    }

    /**
     * Obtener una relación DoctorArea por ID
     * @param id ID de la relación
     * @return DoctorArea encontrada
     */
    @Transactional(readOnly = true)
    public ApiResponse<DoctorAreaResponseDTO> findById(Integer id) {
        try {
            DoctorAreaResponseDTO doctorArea = doctorAreaRepository.findById(id)
                    .map(doctorAreaMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Relación DoctorArea no encontrada con ID: " + id));

            return ApiResponse.success(doctorArea, "Relacion encontrada exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Obtener las áreas de evaluación de un doctor específico
     * @param doctorId ID del doctor
     * @return Lista de DoctorArea del doctor
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorAreaSummaryDTO>> findByDoctorId(Integer doctorId) {
        try{
            //Validar que el id del doctor exista
            if(!doctorRepository.existsById(doctorId)){
                return ApiResponse.error("No se encontro un doctor con id : " + doctorId);
            }

            List<DoctorAreaSummaryDTO> relations = this.doctorAreaRepository.findByDoctorId(doctorId)
                    .stream().map(doctorAreaMapper::toSummaryDTO).toList();

            if (relations.isEmpty()) {
                return ApiResponse.success(relations, "No se encontraron áreas de evaluación para este doctor");
            }

            return ApiResponse.success(relations, "Relaciones encontradas exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }

    }

    /**
     * Obtener los doctores de un área de evaluación específica
     * @param evaluationAreaId ID del área de evaluación
     * @return Lista de DoctorArea en el área
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorAreaSummaryDTO>> findByEvaluationAreaId(Integer evaluationAreaId) {
        try{
            //Validar que el id del area exista
            if(!evaluationAreaRepository.existsById(evaluationAreaId)){
                return ApiResponse.error("No se encontro un area de evaluacion con id : " + evaluationAreaId);
            }

            List<DoctorAreaSummaryDTO> relations = this.doctorAreaRepository.findByEvaluationAreaId(evaluationAreaId)
                    .stream().map(doctorAreaMapper::toSummaryDTO).toList();

            if (relations.isEmpty()){
                return ApiResponse.success(relations, "No se encontraron doctores para esta área de evaluación");
            }

            return ApiResponse.success(relations, "Relaciones encontradas exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Crear una nueva relación DoctorArea
     * @param doctorAreaCreateDTO DTO con los datos de la relación
     * @return ApiResponse<DoctorArea> con el resultado de la operación
     */
    @Transactional
    public ApiResponse<DoctorAreaResponseDTO> create(DoctorAreaCreateDTO doctorAreaCreateDTO) {
        try {
            if (doctorAreaCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            Integer doctorId = doctorAreaCreateDTO.getDoctorId();
            Integer evaluationAreaId = doctorAreaCreateDTO.getEvaluationAreaId();

            // Validar que exista el doctor
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + doctorId));

            // Validar que exista el área de evaluación
            EvaluationArea evaluationArea = evaluationAreaRepository.findById(evaluationAreaId)
                    .orElseThrow(() -> new IllegalArgumentException("Área de evaluación no encontrada con ID: " + evaluationAreaId));

            // Validar que no exista ya esta relación
            if (doctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(doctorId, evaluationAreaId)) {
                return ApiResponse.error("Este doctor ya está asociado a esta área de evaluación");
            }
            DoctorArea doctorArea = doctorAreaMapper.toEntity(doctorAreaCreateDTO);
            doctorArea.setDoctor(doctor);
            doctorArea.setEvaluationArea(evaluationArea);

            evaluationArea.setDoctorsCount(evaluationArea.getDoctorsCount() + 1); // Incrementamos el contador de doctores en el área

            return ApiResponse.success(doctorAreaMapper.toResponseDTO(doctorAreaRepository.save(doctorArea)), "Doctor asociado al área de evaluación exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Eliminar una relación DoctorArea por ID
     * @param id ID de la relación a eliminar
     * @return ApiResponse<Void> con el resultado de la operación
     */
    public ApiResponse<Void> delete(Integer id) {
        try {
            DoctorArea doctorArea = doctorAreaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Relación DoctorArea no encontrada con ID: " + id));

            doctorArea.getEvaluationArea().setDoctorsCount(doctorArea.getEvaluationArea().getDoctorsCount() - 1); // Decrementamos el contador de doctores en el área

            doctorAreaRepository.delete(doctorArea);
            return ApiResponse.success("Relación DoctorArea eliminada exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Eliminar la relación entre un doctor y un área de evaluación
     * @param doctorId ID del doctor
     * @param evaluationAreaId ID del área de evaluación
     * @return ApiResponse<Void> con el resultado de la operación
     */
    public ApiResponse<Void> deleteByDoctorAndArea(Integer doctorId, Integer evaluationAreaId) {
        try {
            DoctorArea doctorArea = doctorAreaRepository.findByDoctorIdAndEvaluationAreaId(doctorId, evaluationAreaId).orElse(null);

            if (doctorArea == null) {
                return ApiResponse.error("No existe relación entre este doctor y esta área de evaluación");
            }

            doctorArea.getEvaluationArea().setDoctorsCount(doctorArea.getEvaluationArea().getDoctorsCount() - 1); // Decrementamos el contador de doctores en el área
            doctorAreaRepository.delete(doctorArea);
            return ApiResponse.success("Doctor disociado del área de evaluación exitosamente");

        } catch (Exception ex) {
            return ApiResponse.error("Error al disociar doctor del área de evaluación: " + ex.getMessage());
        }
    }

}

