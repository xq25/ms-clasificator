//package Backend.ms_clasificator.Mappers.MedicalImgMappers;
//
//import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgCreateDTO;
//import Backend.ms_clasificator.Mappers.Mapper;
//import Backend.ms_clasificator.Models.MedicalImg;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MedicalImgMapper implements Mapper<MedicalImg, MedicalImgCreateDTO> {
//    @Override
//    public MedicalImg toEntity(MedicalImgCreateDTO medicalImgCreateDTO) {
//        if (medicalImgCreateDTO == null) {
//            return null;
//        }
//
//        return MedicalImg.builder()
//                .url(medicalImgCreateDTO.getUrl())
//                // Las relaciones se asignan en el Service
//                .build();
//    }
//
//    @Override
//    public MedicalImgCreateDTO toDTO(MedicalImg medicalImg) {
//        if (medicalImg == null) {
//            return null;
//        }
//
//        return MedicalImgCreateDTO.builder()
//                .url(medicalImg.getUrl())
//                .evaluationAreaId(medicalImg.getEvaluationAreaId() != null ? medicalImg.getEvaluationAreaId() : null)
//                .patientId(medicalImg.getPatientId() != null ? medicalImg.getPatientId() : null)
//                .build();
//    }
//}
//
