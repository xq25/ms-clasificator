package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Doctor.DoctorBaseDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorResponseDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorSummaryDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.DoctorMappers.DoctorMapper;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.UserInfo;
import Backend.ms_clasificator.Repositories.DoctorRepository;
import Backend.ms_clasificator.Repositories.ImageDiagnosticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private ImageDiagnosticRepository imageDiagnosticRepository;

    @Autowired
    private SecurityServices securityServices;

    /**
     * Obtener todos los doctores
     * @return Lista de todos los doctores
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DoctorSummaryDTO>> findAll() {
        List<DoctorSummaryDTO> doctors = this.doctorRepository.findAll()
                .stream().map(doctorMapper::toSummaryDTO).toList();

        return ApiResponse.success(doctors, "Doctores encontrados exitosamente");
    }

    /**
     * Obtener un doctor por ID
     * @param id ID del doctor
     * @return Doctor encontrado
     * @throws IllegalArgumentException si el doctor no existe
     */
    @Transactional(readOnly = true)
    public ApiResponse<DoctorResponseDTO> findById(Integer id) {
        try{
            DoctorResponseDTO doctor =  doctorRepository.findById(id)
                    .map(doctorMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + id));

            UserInfo userInfo = securityServices.getUserInfo(doctor.getUserId());
            if(userInfo != null){
                doctor.setUserInfo(userInfo);
            }

            return ApiResponse.success(doctor, "Doctor encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Buscar un doctor por código
     * @param code Código del doctor
     * @return Doctor encontrado o null
     */
    @Transactional(readOnly = true)
    public ApiResponse<DoctorResponseDTO> findByCode(String code) {
        try{
            DoctorResponseDTO doctor =  doctorRepository.findByCode(code)
                    .map(doctorMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con código: " + code));

            UserInfo userInfo = securityServices.getUserInfo(doctor.getUserId());
            if(userInfo != null){
                doctor.setUserInfo(userInfo);
            }

            return ApiResponse.success(doctor, "Doctor encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<DoctorResponseDTO> findByUserId(String userId) {
        try{
            DoctorResponseDTO doctor =  doctorRepository.findByUserId(userId)
                    .map(doctorMapper::toResponseDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con userId: " + userId));

            UserInfo userInfo = securityServices.getUserInfo(doctor.getUserId());
            if(userInfo != null){
                doctor.setUserInfo(userInfo);
            }

            return ApiResponse.success(doctor, "Doctor encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Crear un nuevo doctor
     * @param doctorCreateDTO DTO con datos de entrada
     * @return ApiResponse<Doctor> con el resultado de la operación
     */
    public ApiResponse<DoctorSummaryDTO> create(DoctorBaseDTO doctorCreateDTO) {
        if (doctorCreateDTO == null) {
            return ApiResponse.error("El DTO no puede ser nulo");
        }

        // Validar que no exista doctor con el mismo código
        if(this.doctorRepository.existsByCode(doctorCreateDTO.getCode())){
            return ApiResponse.error("Ya existe un doctor con el código: " + doctorCreateDTO.getCode() + ". El código debe ser único.");
        }

        // Validar con ms-security que exista ese usuario
        if(!securityServices.existUserById(doctorCreateDTO.getUserId())){
            return ApiResponse.error("No existe un usuario con el userId: " + doctorCreateDTO.getUserId() + ". No se puede crear el doctor sin un usuario asociado.");
        }

        // Validar que no exista doctor con el mismo userId
        if(this.doctorRepository.existsByUserId(doctorCreateDTO.getUserId())){
            return ApiResponse.error("Ya existe un doctor asociado al userId: " + doctorCreateDTO.getUserId() + ". Un usuario solo puede estar asociado a un doctor.");
        }

        // Asignamos el rol a este usuario para que pueda acceder a los endpoints de doctor
        boolean roleAssing = this.securityServices.assignDefaultRole(doctorCreateDTO.getUserId(), "doctor");
        String roleInfo = roleAssing ? "Rol 'doctor' asignado correctamente al usuario." : "No se pudo asignar el rol 'doctor' al usuario.";

        Doctor doctor = doctorMapper.toEntity(doctorCreateDTO);

        // Si se asigna o no el rol por defecto, igual generamos el usuario y mosstramos la info pertinente
        return ApiResponse.success(doctorMapper.toSummaryDTO(doctorRepository.save(doctor)), "Doctor creado exitosamente. " + roleInfo);

    }

    /**
     * Actualizar un doctor existente
     * @param id ID del doctor a actualizar
     * @param doctorUpdateDTO DTO con datos a actualizar
     * @return ApiResponse<Doctor> con el resultado de la operación
     */
    public ApiResponse<DoctorSummaryDTO> update(Integer id, DoctorUpdateDTO doctorUpdateDTO) {
        try {
            if (doctorUpdateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            Doctor doctor = doctorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + id));

            // Actualizar solo los campos que vienen en el DTO
            // Si están disponibles en DoctorUpdateDTO, se actualizan aquí
            doctor.setCode(doctorUpdateDTO.getCode() != null ? doctorUpdateDTO.getCode() : doctor.getCode());

            return ApiResponse.success(doctorMapper.toSummaryDTO(doctorRepository.save(doctor)), "Doctor actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    /**
     * Eliminar un doctor por ID
     * @param id ID del doctor a eliminar
     * @throws IllegalArgumentException si el doctor no existe
     */
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        try {
            Doctor doctor = doctorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + id));

            // Validamos que el medico no tenga diagnosticos de imagenes asociados.
            if (imageDiagnosticRepository.existsByDoctorId(doctor.getId())) {
                return ApiResponse.error("No se puede eliminar el doctor porque tiene diagnosticos asociados");
            }

            doctorRepository.delete(doctor);
            return ApiResponse.success("Doctor eliminado exitosamente");

        }catch (DataIntegrityViolationException ex) {
            return ApiResponse.error("Violacion de integridad en la base de datos: " + ex.getMessage());
        }catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al eliminar doctor: " + ex.getMessage());
        }
    }


}
