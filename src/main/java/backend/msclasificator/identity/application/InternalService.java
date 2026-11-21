package backend.msclasificator.identity.application;

import backend.msclasificator.doctor.domain.Doctor;
import backend.msclasificator.patient.domain.Patient;
import backend.msclasificator.doctor.infrastructure.persistence.DoctorAreaRepository;
import backend.msclasificator.doctor.infrastructure.persistence.DoctorRepository;
import backend.msclasificator.patient.application.port.out.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Este archivo esta destinado a las utilidades de informacion necesarias para otros ms
@Service
public class InternalService {
    @Autowired
    private DoctorRepository theDoctorRepository ;

    @Autowired
    private PatientRepository thePatientRepository;

    @Autowired
    private DoctorAreaRepository theDoctorAreaRepository;

    public boolean existRelation(String userId){
        Doctor doctorRelation = this.theDoctorRepository.findByUserId(userId).orElse(null);
        Patient patientRelation = this.thePatientRepository.findByUserId(userId).orElse(null);

        return doctorRelation != null || patientRelation != null;
    }

    public boolean existRelationWithDoctor(String userId){
        Doctor doctorRelation = this.theDoctorRepository.findByUserId(userId).orElse(null);
        return doctorRelation != null;

    }

    public boolean existRelationWithPatient(String userId){
        Patient patientRelation = this.thePatientRepository.findByUserId(userId).orElse(null);
        return patientRelation != null;

    }

    public boolean existsDoctorInArea(Integer doctorId, Integer evaluationAreaId){
        return this.theDoctorAreaRepository.existsByDoctorIdAndEvaluationAreaId(doctorId, evaluationAreaId);
    }
}
