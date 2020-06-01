package com.example.demo.utils.aws;

import java.io.IOException;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.example.demo.config.ApplicationProperties;
import com.example.demo.utils.DataLoader;
import com.example.demo.utils.DataLoaderInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataLoaderFromAWS extends DataLoader implements DataLoaderInterface{

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private AmazonS3 amazonS3Client;

    @Override
    public Map<String, Long> loadData() {
        try {
            log.info("Listing objects");
            helper.processDataAwsV2(jsonObjectList, words, amazonS3Client, applicationProperties.getAwsServices().getBucketName());

            return words;
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}
