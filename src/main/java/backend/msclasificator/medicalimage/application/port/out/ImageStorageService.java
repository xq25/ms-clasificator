package backend.msclasificator.medicalimage.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    String uploadImage(MultipartFile file, String folder);

    String generatePublicUrl(String imageKey);

    void deleteImage(String imageKey);

    boolean imageExists(String imageKey);

    String getProviderName();
}