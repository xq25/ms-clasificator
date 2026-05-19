//package Backend.ms_clasificator.infrastructure.storage.s3;
//
//import Backend.ms_clasificator.domain.port.ImageStoragePort;
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
///**
// * Adaptador S3/R2 — implementación de producción del puerto ImageStoragePort.
// *
// * COMPATIBILIDAD:
// * - AWS S3: solo configura la región y credenciales normales.
// * - Cloudflare R2: configura endpoint customizado en StorageConfig.
// *   R2 es 100% compatible con la API de S3 — solo cambia el endpoint URL.
// *
// * DECISIÓN ARQUITECTÓNICA — Por qué NO auto-crear buckets aquí:
// * En producción los buckets se crean por IaC (Terraform/CDK).
// * La app no debe tener permisos de creación de buckets en prod (principio
// * de mínimo privilegio). Si el bucket no existe, falla rápido con error claro.
// *
// * DECISIÓN — URLs públicas vs pre-signed:
// * Si el bucket es PÚBLICO → generamos URL directa sin pre-sign (más performante).
// * Si el bucket es PRIVADO → pre-signed URLs con expiración (más seguro para médico).
// * Controlado por el flag `storage.s3.public-bucket` en application-prod.yml.
// */
//@Slf4j
//public class S3ImageStorageAdapter implements ImageStoragePort {
//
//    private final S3Client s3Client;
//    private final S3Presigner s3Presigner;
//    private final String bucketName;
//    private final boolean isPublicBucket;
//    private final String publicBaseUrl;  // Solo si isPublicBucket=true
//    private final long presignedUrlExpiryHours;
//    private static final String PROVIDER = "s3";
//
//    public S3ImageStorageAdapter(S3Client s3Client,
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
//        ImageStorageUtils.validateImageSize(file, 10 * 1024 * 1024); // 10 MB
//
//        String imageKey = ImageStorageUtils.generateImageKey(file.getOriginalFilename(), folder);
//
//        try {
//            PutObjectRequest request = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(imageKey)
//                    .contentType(file.getContentType())
//                    .contentLength(file.getSize())
//                    // En imágenes médicas privadas: NO ponemos ACL public-read
//                    // Si el bucket es público, ya hereda eso del bucket policy
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
//        // Opción 1: bucket público — URL directa, sin expiración
//        if (isPublicBucket) {
//            return publicBaseUrl + "/" + imageKey;
//        }
//
//        // Opción 2: bucket privado — URL pre-signed con expiración
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