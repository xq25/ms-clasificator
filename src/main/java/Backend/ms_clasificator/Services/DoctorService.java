package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.Doctor.CreateDoctorDTO;
import Backend.ms_clasificator.DTOs.Doctor.UpdateDoctorDTO;
import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Repositories.DoctorRepository;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository theDoctorRepository;

    @Autowired
    private EvaluationAreaRepository theEvaluationAreaRepository;

    // FindAll

    public List<Doctor> getAll() {
        return this.theDoctorRepository.findAll();
    }

    // FindOne
    public Doctor getById(String code) {
        return this.theDoctorRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado: " + code));
    }

    // CREATE

    public Doctor create(CreateDoctorDTO dto) {
        // Unicidad: no puede existir otro doctor con el mismo userId
        if (this.theDoctorRepository.existsByUserId(dto.getUserId())) {
            throw new RuntimeException("Ya existe un doctor asociado al userId: " + dto.getUserId());
        }

        // Verificar que el área de evaluación exista
        EvaluationArea area = this.theEvaluationAreaRepository.findById(dto.getEvaluationAreaId())
                .orElseThrow(() -> new RuntimeException("Área de evaluación no encontrada: " + dto.getEvaluationAreaId()));

        return this.theDoctorRepository.save(new Doctor(dto.getCode(), dto.getUserId(), area));
    }

    //  UPDATE

    public Doctor update(String code, UpdateDoctorDTO dto) {
        Doctor doctor = this.theDoctorRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado: " + code));

        // Si cambia el userId, validar que el nuevo no esté en uso por otro doctor
        if (!doctor.getUserId().equals(dto.getUserId()) &&
                this.theDoctorRepository.existsByUserId(dto.getUserId())) {
            throw new RuntimeException("El userId ya está asociado a otro doctor: " + dto.getUserId());
        }

        // Verificar que el área de evaluación exista
        EvaluationArea area = this.theEvaluationAreaRepository.findById(dto.getEvaluationAreaId())
                .orElseThrow(() -> new RuntimeException("Área de evaluación no encontrada: " + dto.getEvaluationAreaId()));

        doctor.setUserId(dto.getUserId());
        doctor.setEvaluationArea(area);

        return this.theDoctorRepository.save(doctor);
    }

    //  DELETE

    public void delete(String code) {
        if (!this.theDoctorRepository.existsById(code)) {
            throw new RuntimeException("Doctor no encontrado: " + code);
        }
        this.theDoctorRepository.deleteById(code);
    }
}