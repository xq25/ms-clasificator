package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.Models.Doctor;
import Backend.ms_clasificator.Models.Patient;
import Backend.ms_clasificator.Repositories.DoctorRepository;
import Backend.ms_clasificator.Repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Este archivo esta destinado a las utilidades de informacion necesarias para otros ms
@Service
public class InternalService {
    @Autowired
    private DoctorRepository theDoctorRepository ;

    @Autowired
    private PatientRepository thePatientRepository;

    public boolean existRelation(String userId){
        Doctor doctorRelation = this.theDoctorRepository.findByUserId(userId);
        Patient patientRelation = this.thePatientRepository.findByUserId(userId);
        boolean validation = false;

        if (doctorRelation != null || patientRelation != null){
            validation = true;
        }
        return validation;
    }

    public boolean existRelationWithDoctor(String userId){
        Doctor doctorRelation = this.theDoctorRepository.findByUserId(userId);
        return doctorRelation != null;

    }

    public boolean existRelationWithPatient(String userId){
        Patient patientRelation = this.thePatientRepository.findByUserId(userId);
        return patientRelation != null;

    }
}
