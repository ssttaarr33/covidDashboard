package com.example.demo.utils.aws;

import java.io.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AWSRepositoryImpl implements AWSRepository {

    @Override
    public void uploadFileToS3Bucket(String fileName, File file, String bucketName, AmazonS3 amazonS3Client) {
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
    }

    @Override
    public S3Object downloadFileFromS3Bucket(String fileName, String bucketName, AmazonS3 amazonS3Client) {
        return amazonS3Client.getObject(bucketName, fileName);
    }

    @Override
    public void deleteFileFromS3Bucket(String fileName, String bucketName, AmazonS3 amazonS3Client) {
        amazonS3Client.deleteObject(bucketName, fileName);
    }

}
