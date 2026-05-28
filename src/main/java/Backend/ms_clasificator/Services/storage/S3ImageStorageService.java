package Backend.ms_clasificator.Services.storage;

//import Backend.ms_clasificator.exceptions.ImageStorageException;
//import Backend.ms_clasificator.util.ImageStorageUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.*;
//import software.amazon.awssdk.services.s3.presigner.S3Presigner;
//import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
//import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
//
//import java.io.IOException;
//import java.time.Duration;
//
//@Slf4j
//public class S3ImageStorageService implements ImageStorageService {
//
//    private final S3Client s3Client;
//    private final S3Presigner s3Presigner;
//    private final String bucketName;
//    private final boolean isPublicBucket;
//    private final String publicBaseUrl;
//    private final long presignedUrlExpiryHours;
//    private static final String PROVIDER = "s3";
//
//    public S3ImageStorageService(S3Client s3Client,
//                                 S3Presigner s3Presigner,
//                                 String bucketName,
//                                 boolean isPublicBucket,
//                                 String publicBaseUrl,
//                                 long presignedUrlExpiryHours) {
//        this.s3Client = s3Client;
//        this.s3Presigner = s3Presigner;
//        this.bucketName = bucketName;
//        this.isPublicBucket = isPublicBucket;
//        this.publicBaseUrl = publicBaseUrl;
//        this.presignedUrlExpiryHours = presignedUrlExpiryHours;
//    }
//
//    @Override
//    public String uploadImage(MultipartFile file, String folder) {
//        ImageStorageUtils.validateImageType(file);
//        ImageStorageUtils.validateImageSize(file, 10 * 1024 * 1024);
//
//        String imageKey = ImageStorageUtils.generateImageKey(file.getOriginalFilename(), folder);
//
//        try {
//            PutObjectRequest request = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(imageKey)
//                    .contentType(file.getContentType())
//                    .contentLength(file.getSize())
//                    .build();
//
//            s3Client.putObject(request, RequestBody.fromInputStream(
//                    file.getInputStream(), file.getSize()
//            ));
//
//            log.info("[S3] Imagen subida: bucket={}, key={}", bucketName, imageKey);
//            return imageKey;
//
//        } catch (IOException e) {
//            throw new ImageStorageException("Error leyendo el archivo de entrada", PROVIDER, e);
//        } catch (S3Exception e) {
//            log.error("[S3] Error subiendo imagen: {}", e.awsErrorDetails().errorMessage());
//            throw new ImageStorageException("No se pudo subir la imagen a S3: " + e.awsErrorDetails().errorMessage(), PROVIDER, e);
//        }
//    }
//
//    @Override
//    public String generatePublicUrl(String imageKey) {
//        if (isPublicBucket) {
//            return publicBaseUrl + "/" + imageKey;
//        }
//
//        try {
//            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                    .signatureDuration(Duration.ofHours(presignedUrlExpiryHours))
//                    .getObjectRequest(r -> r.bucket(bucketName).key(imageKey))
//                    .build();
//
//            PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
//            log.debug("[S3] Pre-signed URL generada para key={}, expiry={}h", imageKey, presignedUrlExpiryHours);
//            return presigned.url().toString();
//
//        } catch (Exception e) {
//            throw new ImageStorageException("No se pudo generar URL pre-signed para: " + imageKey, PROVIDER, e);
//        }
//    }
//
//    @Override
//    public void deleteImage(String imageKey) {
//        try {
//            s3Client.deleteObject(DeleteObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(imageKey)
//                    .build()
//            );
//            log.info("[S3] Imagen eliminada: key={}", imageKey);
//
//        } catch (S3Exception e) {
//            log.error("[S3] Error eliminando imagen: {}", e.awsErrorDetails().errorMessage());
//            throw new ImageStorageException("No se pudo eliminar imagen: " + imageKey, PROVIDER, e);
//        }
//    }
//
//    @Override
//    public boolean imageExists(String imageKey) {
//        try {
//            s3Client.headObject(HeadObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(imageKey)
//                    .build()
//            );
//            return true;
//        } catch (NoSuchKeyException e) {
//            return false;
//        } catch (S3Exception e) {
//            log.warn("[S3] Error verificando existencia de {}: {}", imageKey, e.getMessage());
//            return false;
//        }
//    }
//
//    @Override
//    public String getProviderName() {
//        return PROVIDER;
//    }
//}