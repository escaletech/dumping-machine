package com.grupozap.dumping_machine.uploaders;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;

public class S3UploaderEncrypted implements Uploader {
    private final String bucketName;
    private final String bucketRegion;
    private final static String TYPE = "s3a";

    public S3UploaderEncrypted(String bucketName, String bucketRegion) {
        this.bucketName = bucketName;
        this.bucketRegion = bucketRegion;
    }

    public void upload(String remotePath, String filename) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(this.bucketRegion)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        PutObjectRequest request = new PutObjectRequest(this.bucketName, remotePath, new File(filename));

        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

        request.setMetadata(objectMetadata);

        s3Client.putObject(request);
    }
    public String getServerPath() {
        return TYPE + "://" + bucketName;
    }
}
