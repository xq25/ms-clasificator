package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Doctor.DoctorBaseDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorResponseDTO;
import Backend.ms_clasificator.DTOs.Doctor.DoctorUpdateDTO;
import Backend.ms_clasificator.DTOs.Response.ApiResponse;
import Backend.ms_clasificator.Mappers.DoctorMappers.DoctorMapper;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.ImageDiagnostic;
import Backend.ms_clasificator.Repositories.DoctorRepository;
import Backend.ms_clasificator.Repositories.ImageDiagnosticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    public ApiResponse<List<DoctorResponseDTO>> findAll() {
        try {
            java.util.List<Doctor> doctors = doctorRepository.findAll();
            java.util.List<Backend.ms_clasificator.DTOs.Doctor.DoctorResponseDTO> dtos = new java.util.ArrayList<>();
            for (Doctor d : doctors) {
                dtos.add(doctorMapper.toResponseDTO(d));
            }
            return ApiResponse.success(dtos, "Doctores encontrados exitosamente");
        } catch (Exception ex) {
            return ApiResponse.error("Error al listar doctores: " + ex.getMessage());
        }
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
            Doctor doctor =  doctorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + id));

            return ApiResponse.success(doctorMapper.toResponseDTO(doctor), "Doctor encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar doctor: " + ex.getMessage());
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
            Doctor doctor =  doctorRepository.findByCode(code);

            if (doctor == null){
                throw new IllegalArgumentException("Doctor no encontrado con código: " + code);
            }
            return ApiResponse.success(doctorMapper.toResponseDTO(doctor), "Doctor encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar doctor: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<DoctorResponseDTO> findByUserId(String userId) {
        try{
            Doctor doctor =  doctorRepository.findByUserId(userId);

            if (doctor == null){
                throw new IllegalArgumentException("Doctor no encontrado con userId: " + userId);
            }
            return ApiResponse.success(doctorMapper.toResponseDTO(doctor), "Doctor encontrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al buscar doctor: " + ex.getMessage());
        }
    }

    /**
     * Crear un nuevo doctor
     * @param doctorCreateDTO DTO con datos de entrada
     * @return ApiResponse<Doctor> con el resultado de la operación
     */
    public ApiResponse<DoctorResponseDTO> create(DoctorBaseDTO doctorCreateDTO) {
        try {
            if (doctorCreateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            // Validar que no exista doctor con el mismo código
            if (doctorRepository.findByCode(doctorCreateDTO.getCode()) != null) {
                return ApiResponse.error("Ya existe un doctor con el código: " + doctorCreateDTO.getCode());
            }

            // Validar con ms-security que exista ese usuario
            boolean exists = securityServices.existUserById(doctorCreateDTO.getUserId());

            if (!exists) {
                return ApiResponse.error("El usuario no existe con este user_id" + doctorCreateDTO.getUserId());
            }

            // Validar que no exista doctor con el mismo userId
            Doctor doctorByUserId = doctorRepository.findByUserId(doctorCreateDTO.getUserId());
            if (doctorByUserId != null) {
                return ApiResponse.error("Ya existe un doctor con el userId: " + doctorCreateDTO.getUserId() + ". El userId debe ser único.");
            }
            // Asignamos el rol a este usuario para que pueda acceder a los endpoints de doctor
            boolean roleAssing = this.securityServices.assignDefaultRole(doctorCreateDTO.getUserId(), "doctor");
            String roleInfo = roleAssing ? "Rol 'doctor' asignado correctamente al usuario." : "No se pudo asignar el rol 'doctor' al usuario.";


            Doctor doctor = doctorMapper.toEntity(doctorCreateDTO);
            Doctor saved = doctorRepository.save(doctor);

            // Si hay algun error debemos de mostrarlo, pero realmente para llegar a este punto el unico fallo seria para asignar el rol, por los cual solo agregariamos al mensaje que no se pudo asignar el role
            return ApiResponse.success(doctorMapper.toResponseDTO(saved), "Doctor creado exitosamente. " + roleInfo);

        } catch (Exception ex) {
            return ApiResponse.error("Error al crear doctor: " + ex.getMessage());
        }
    }

    /**
     * Actualizar un doctor existente
     * @param id ID del doctor a actualizar
     * @param doctorUpdateDTO DTO con datos a actualizar
     * @return ApiResponse<Doctor> con el resultado de la operación
     */
    public ApiResponse<DoctorResponseDTO> update(Integer id, DoctorUpdateDTO doctorUpdateDTO) {
        try {
            if (doctorUpdateDTO == null) {
                return ApiResponse.error("El DTO no puede ser nulo");
            }

            Doctor doctor = doctorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + id));

            // Actualizar solo los campos que vienen en el DTO
            // Si están disponibles en DoctorUpdateDTO, se actualizan aquí
            doctor.setCode(doctorUpdateDTO.getCode() != null ? doctorUpdateDTO.getCode() : doctor.getCode());

            Doctor updated = doctorRepository.save(doctor);
            return ApiResponse.success(doctorMapper.toResponseDTO(updated), "Doctor actualizado exitosamente");

        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.error("Error al actualizar doctor: " + ex.getMessage());
        }
    }

    /**
     * Eliminar un doctor por ID
     * @param id ID del doctor a eliminar
     * @throws IllegalArgumentException si el doctor no existe
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiResponse<Void> delete(Integer id) {
        try {
            Doctor doctor = doctorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + id));

            // Validamos que el medico no tenga diagnosticos de imagenes asociados.
            List<ImageDiagnostic> imageDiagnostics = this.imageDiagnosticRepository.findByDoctor_Id(id);
            if (!imageDiagnostics.isEmpty()){
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
