package Backend.ms_clasificator.configurations;

import Backend.ms_clasificator.Services.storage.ImageStorageService;
import Backend.ms_clasificator.Services.storage.MinioImageStorageService;
//import Backend.ms_clasificator.Services.storage.S3ImageStorageService;
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

//import java.net.URI;

@Slf4j
@Configuration
public class StorageConfig {

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
    public ImageStorageService imageStorageService(
            MinioClient minioClient,
            @Value("${storage.minio.bucket}") String bucket,
            @Value("${storage.minio.public-endpoint:${storage.minio.endpoint}}") String publicEndpoint) {

        log.info("[Storage] Provider activo: MinIO | bucket={} | publicEndpoint={}", bucket, publicEndpoint);
        return new MinioImageStorageService(minioClient, bucket, publicEndpoint);
    }

    // ============================================================
    // PERFIL PROD — AWS S3 o Cloudflare R2 (descomentar cuando se necesite)
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
//        if (!endpoint.isBlank()) {
//            log.info("[Storage] Usando endpoint custom S3-compatible: {}", endpoint);
//            builder
//                    .endpointOverride(URI.create(endpoint))
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
//    public ImageStorageService imageStorageServiceProd(
//            S3Client s3Client,
//            S3Presigner s3Presigner,
//            @Value("${storage.s3.bucket}") String bucket,
//            @Value("${storage.s3.public-bucket:false}") boolean isPublicBucket,
//            @Value("${storage.s3.public-base-url:}") String publicBaseUrl,
//            @Value("${storage.s3.presigned-url-expiry-hours:24}") long expiryHours) {
//
//        log.info("[Storage] Provider activo: S3/R2 | bucket={} | public={}", bucket, isPublicBucket);
//        return new S3ImageStorageService(s3Client, s3Presigner, bucket, isPublicBucket, publicBaseUrl, expiryHours);
//    }
}