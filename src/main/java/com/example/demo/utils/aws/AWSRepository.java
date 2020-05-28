package com.example.demo.utils.aws;

import java.io.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

public interface AWSRepository {

    void uploadFileToS3Bucket(String fileName, File file, String bucketName, AmazonS3 amazonS3Client);

    S3Object downloadFileFromS3Bucket(String fileName, String bucketName, AmazonS3 amazonS3Client);

    void deleteFileFromS3Bucket(String fileName, String bucketName, AmazonS3 amazonS3Client);
}
