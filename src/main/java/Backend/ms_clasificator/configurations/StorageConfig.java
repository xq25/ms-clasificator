package Backend.ms_clasificator.configurations;

import Backend.ms_clasificator.domain.port.ImageStoragePort;
import Backend.ms_clasificator.infrastructure.storage.minio.MinioImageStorageAdapter;
//import Backend.ms_clasificator.infrastructure.storage.s3.S3ImageStorageAdapter;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.S3Configuration;
//import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Configuración de beans de storage.
 *
 * DECISIÓN ARQUITECTÓNICA — Por qué @Profile y no @ConditionalOnProperty:
 * Los profiles de Spring son el mecanismo idiomático para separar entornos.
 * application-dev.yml activa MinIO, application-prod.yml activa S3/R2.
 * El equipo solo cambia SPRING_PROFILES_ACTIVE=prod en el deployment.
 *
 * Si prefieres @ConditionalOnProperty (ej: storage.provider=minio),
 * también funciona — pero profiles es más explícito y menos propenso a error.
 *
 * MIGRACIÓN ENTRE PROVIDERS:
 * 1. Los beans están aquí — el resto del código usa ImageStoragePort.
 * 2. Cambiar de MinIO a S3: cambiar el profile activo. FIN.
 * 3. MedicalImageService no toca nada.
 */
@Slf4j
@Configuration
public class StorageConfig {

    // ============================================================
    // PERFIL DEV — MinIO local
    // ============================================================

    @Bean
    @Profile("dev")
    public MinioClient minioClient(
            @Value("${storage.minio.endpoint}") String endpoint,
            @Value("${storage.minio.access-key}") String accessKey,
            @Value("${storage.minio.secret-key}") String secretKey) {

        log.info("[Storage] Inicializando cliente MinIO: endpoint={}", endpoint);
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    @Profile("dev")
    public ImageStoragePort imageStoragePort(
            MinioClient minioClient,
            @Value("${storage.minio.bucket}") String bucket,
            @Value("${storage.minio.endpoint}") String endpoint) {

        log.info("[Storage] Provider activo: MinIO | bucket={}", bucket);
        return new MinioImageStorageAdapter(minioClient, bucket, endpoint);
    }
}

    // ============================================================
    // PERFIL PROD — AWS S3 o Cloudflare R2
    // ============================================================

//    @Bean
//    @Profile("prod")
//    public S3Client s3Client(
//            @Value("${storage.s3.access-key}") String accessKey,
//            @Value("${storage.s3.secret-key}") String secretKey,
//            @Value("${storage.s3.region}") String region,
//            @Value("${storage.s3.endpoint:}") String endpoint) {
//
//        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
//
//        var builder = S3Client.builder()
//                .credentialsProvider(StaticCredentialsProvider.create(credentials))
//                .region(Region.of(region));
//
//        // Si hay endpoint custom → es Cloudflare R2 u otro S3-compatible
//        if (!endpoint.isBlank()) {
//            log.info("[Storage] Usando endpoint custom S3-compatible: {}", endpoint);
//            builder
//                    .endpointOverride(URI.create(endpoint))
//                    // R2 requiere path-style para funcionar bien
//                    .serviceConfiguration(S3Configuration.builder()
//                            .pathStyleAccessEnabled(true)
//                            .build());
//        }
//
//        return builder.build();
//    }
//
//    @Bean
//    @Profile("prod")
//    public S3Presigner s3Presigner(
//            @Value("${storage.s3.access-key}") String accessKey,
//            @Value("${storage.s3.secret-key}") String secretKey,
//            @Value("${storage.s3.region}") String region,
//            @Value("${storage.s3.endpoint:}") String endpoint) {
//
//        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
//
//        var builder = S3Presigner.builder()
//                .credentialsProvider(StaticCredentialsProvider.create(credentials))
//                .region(Region.of(region));
//
//        if (!endpoint.isBlank()) {
//            builder.endpointOverride(URI.create(endpoint));
//        }
//
//        return builder.build();
//    }
//
//    @Bean
//    @Profile("prod")
//    public ImageStoragePort imageStoragePortProd(
//            S3Client s3Client,
//            S3Presigner s3Presigner,
//            @Value("${storage.s3.bucket}") String bucket,
//            @Value("${storage.s3.public-bucket:false}") boolean isPublicBucket,
//            @Value("${storage.s3.public-base-url:}") String publicBaseUrl,
//            @Value("${storage.s3.presigned-url-expiry-hours:24}") long expiryHours) {
//
//        log.info("[Storage] Provider activo: S3/R2 | bucket={} | public={}", bucket, isPublicBucket);
//        return new S3ImageStorageAdapter(s3Client, s3Presigner, bucket, isPublicBucket, publicBaseUrl, expiryHours);
//    }
//}