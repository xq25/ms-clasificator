package Backend.ms_clasificator.Services.storage;

import Backend.ms_clasificator.exceptions.ImageStorageException;
import Backend.ms_clasificator.util.ImageStorageUtils;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MinioImageStorageService implements ImageStorageService {

    private final MinioClient minioClient;
    private final String bucketName;
    private final String publicEndpoint;
    private static final String PROVIDER = "minio";

    public MinioImageStorageService(MinioClient minioClient,
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
        ImageStorageUtils.validateImageSize(file, 10 * 1024 * 1024);

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
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(imageKey)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
            // Replace internal Docker endpoint with the public one (nginx proxy).
            // nginx sets Host: minio when forwarding, so the presigned signature remains valid.
            java.net.URI uri = java.net.URI.create(url);
            String internalBase = uri.getScheme() + "://" + uri.getAuthority();
            url = url.replace(internalBase, publicEndpoint);
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