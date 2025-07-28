package com.photon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Configuration
public class S3Config {

    @Value("${aws.region:}")
    private String awsRegion;

    @Value("${aws.access-key-id:}")
    private String accessKeyId;

    @Value("${aws.secret-access-key:}")
    private String secretAccessKey;

    @Bean
    @ConditionalOnProperty(name = "storage.type", havingValue = "s3")
    public S3Client s3Client() {
        S3ClientBuilder s3ClientBuilder = S3Client.builder()
                .region(Region.of(awsRegion));

        // Use explicit credentials if provided, otherwise use default provider chain
        if (!accessKeyId.isEmpty() && !secretAccessKey.isEmpty()) {
            AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey)
            );
            s3ClientBuilder.credentialsProvider(credentialsProvider);
        } else {
            // This will use IAM roles, environment variables, or AWS credentials file
            s3ClientBuilder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        return s3ClientBuilder.build();
    }

    // Provide a null S3Client bean when using local storage to satisfy dependency injection
    @Bean
    @ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
    public S3Client nullS3Client() {
        return null;
    }
}