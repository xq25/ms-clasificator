package Backend.ms_clasificator.infrastructure.storage.minio;

import Backend.ms_clasificator.domain.port.ImageStoragePort;
import Backend.ms_clasificator.exceptions.ImageStorageException;
import Backend.ms_clasificator.util.ImageStorageUtils;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Adaptador MinIO — implementación local del puerto ImageStoragePort.
 *
 * DECISIÓN ARQUITECTÓNICA:
 * Esta clase SOLO existe en el perfil "dev". Spring no la instancia en prod.
 * Se registra como bean condicionalmente desde StorageConfig.
 *
 * MinIO es API-compatible con S3, así que si mañana quieres apuntar a S3
 * en dev también, solo cambias el endpoint en application-dev.yml.
 */
@Slf4j
public class MinioImageStorageAdapter implements ImageStoragePort {

    private final MinioClient minioClient;
    private final String bucketName;
    private final String publicEndpoint;
    private static final String PROVIDER = "minio";

    public MinioImageStorageAdapter(MinioClient minioClient,
                                    String bucketName,
                                    String publicEndpoint) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.publicEndpoint = publicEndpoint;
        ensureBucketExists();
    }

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        ImageStorageUtils.validateImageType(file);
        ImageStorageUtils.validateImageSize(file, 10 * 1024 * 1024); // 10 MB

        String imageKey = ImageStorageUtils.generateImageKey(file.getOriginalFilename(), folder);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageKey)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("[MinIO] Imagen subida: bucket={}, key={}", bucketName, imageKey);
            return imageKey;

        } catch (Exception e) {
            log.error("[MinIO] Error subiendo imagen: {}", e.getMessage());
            throw new ImageStorageException("No se pudo subir la imagen a MinIO", PROVIDER, e);
        }
    }

    @Override
    public String generatePublicUrl(String imageKey) {
        try {
            // Pre-signed URL válida por 7 días.
            // En dev con MinIO local, esto te da acceso directo.
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(imageKey)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
            log.debug("[MinIO] URL generada para key={}", imageKey);
            return url;

        } catch (Exception e) {
            log.error("[MinIO] Error generando URL: {}", e.getMessage());
            throw new ImageStorageException("No se pudo generar URL para: " + imageKey, PROVIDER, e);
        }
    }

    @Override
    public void deleteImage(String imageKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageKey)
                            .build()
            );
            log.info("[MinIO] Imagen eliminada: key={}", imageKey);

        } catch (Exception e) {
            log.error("[MinIO] Error eliminando imagen: {}", e.getMessage());
            throw new ImageStorageException("No se pudo eliminar imagen: " + imageKey, PROVIDER, e);
        }
    }

    @Override
    public boolean imageExists(String imageKey) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageKey)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER;
    }

    /**
     * Crea el bucket si no existe.
     * En dev esto es conveniente para no tener que configurar MinIO manualmente.
     *
     * DECISIÓN: En producción (S3Adapter) NO hacemos esto — los buckets
     * en AWS se crean por IaC (Terraform/CloudFormation), no por la app.
     */
    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("[MinIO] Bucket creado: {}", bucketName);
            }
        } catch (Exception e) {
            throw new ImageStorageException("No se pudo verificar/crear el bucket: " + bucketName, PROVIDER, e);
        }
    }
}