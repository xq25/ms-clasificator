package backend.msclasificator.medicalimage.infrastructure.storage;

import backend.msclasificator.medicalimage.application.exception.ImageStorageException;
import backend.msclasificator.medicalimage.application.port.out.ImageFile;
import backend.msclasificator.medicalimage.application.port.out.ImageStorage;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

// Adaptador MinIO del puerto ImageStorage (ADR-012). Lo crea StorageConfig.
@Slf4j
class MinioImageStorage implements ImageStorage {

    private final MinioClient minioClient;
    private final String bucketName;
    private final String publicEndpoint;
    private static final String PROVIDER = "minio";

    MinioImageStorage(MinioClient minioClient,
                      String bucketName,
                      String publicEndpoint) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.publicEndpoint = publicEndpoint;
        ensureBucketExists();
    }

    @Override
    public String upload(ImageFile file, String folder) {
        String imageKey = ImageStorageUtils.generateImageKey(file.originalFilename(), folder);

        // El stream lo cierra quien lo abrió (el controlador)
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageKey)
                            .stream(file.content(), file.size(), -1)
                            .contentType(file.contentType())
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
    public void delete(String imageKey) {
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
    public boolean exists(String imageKey) {
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
    public String providerName() {
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