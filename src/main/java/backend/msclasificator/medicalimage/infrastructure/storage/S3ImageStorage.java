package backend.msclasificator.medicalimage.infrastructure.storage;

// Adaptador S3/R2 del puerto ImageStorage (ADR-012). Comentado hasta incorporar el AWS SDK (ADR-004).

//import backend.msclasificator.medicalimage.application.exception.ImageStorageException;
//import backend.msclasificator.medicalimage.application.port.out.ImageFile;
//import backend.msclasificator.medicalimage.application.port.out.ImageStorage;
//import lombok.extern.slf4j.Slf4j;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.*;
//import software.amazon.awssdk.services.s3.presigner.S3Presigner;
//import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
//import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
//
//import java.time.Duration;
//
//@Slf4j
//class S3ImageStorage implements ImageStorage {
//
//    private final S3Client s3Client;
//    private final S3Presigner s3Presigner;
//    private final String bucketName;
//    private final boolean isPublicBucket;
//    private final String publicBaseUrl;
//    private final long presignedUrlExpiryHours;
//    private static final String PROVIDER = "s3";
//
//    S3ImageStorage(S3Client s3Client,
//                   S3Presigner s3Presigner,
//                   String bucketName,
//                   boolean isPublicBucket,
//                   String publicBaseUrl,
//                   long presignedUrlExpiryHours) {
//        this.s3Client = s3Client;
//        this.s3Presigner = s3Presigner;
//        this.bucketName = bucketName;
//        this.isPublicBucket = isPublicBucket;
//        this.publicBaseUrl = publicBaseUrl;
//        this.presignedUrlExpiryHours = presignedUrlExpiryHours;
//    }
//
//    @Override
//    public String upload(ImageFile file, String folder) {
//        String imageKey = ImageStorageUtils.generateImageKey(file.originalFilename(), folder);
//
//        try {
//            PutObjectRequest request = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(imageKey)
//                    .contentType(file.contentType())
//                    .contentLength(file.size())
//                    .build();
//
//            // El stream lo cierra quien lo abrió (el controlador)
//            s3Client.putObject(request, RequestBody.fromInputStream(file.content(), file.size()));
//
//            log.info("[S3] Imagen subida: bucket={}, key={}", bucketName, imageKey);
//            return imageKey;
//
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
//    public void delete(String imageKey) {
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
//    public boolean exists(String imageKey) {
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
//    public String providerName() {
//        return PROVIDER;
//    }
//}