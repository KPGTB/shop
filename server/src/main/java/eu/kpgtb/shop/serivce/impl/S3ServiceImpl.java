package eu.kpgtb.shop.serivce.impl;

import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.serivce.iface.IS3Service;
import lombok.SneakyThrows;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class S3ServiceImpl implements IS3Service {
    private final S3Client client;

    @SneakyThrows
    public S3ServiceImpl(Properties properties) {
        this.client = S3Client.builder()
                .region(Region.of(properties.getS3Region()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getS3AccessKey(), properties.getS3SecretKey())
                ))
                .endpointOverride(new URI(properties.getS3Endpoint()))
                .build();
    }

    @Override
    public void uploadFile(File file, String key, String bucket) {
        this.client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                file.toPath()
        );
    }

    @SneakyThrows
    @Override
    public void uploadFile(byte[] bytes, String key, String bucket) {
        this.client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                RequestBody.fromBytes(bytes)
        );
    }

    @SneakyThrows
    @Override
    public void uploadFile(String url, String key, String bucket) {
        InputStream in = new URL(url).openStream();
        uploadFile(in.readAllBytes(), key, bucket);
        in.close();
    }

    @Override
    public byte[] getFile(String key, String bucket) {
        return this.client.getObjectAsBytes(
            GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build()
        ).asByteArray();
    }

    @Override
    public InputStream getFileAsIS(String key, String bucket) {
        return this.client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        ).asInputStream();
    }
}
