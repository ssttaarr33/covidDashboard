package com.example.demo.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.s3.AmazonS3;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface FileHelper {

    void removeSeveralStuffV2(Map<String, Long> words);

    Map<String, Long> parseJsonObjectsV2(List<JSONObject> jsonObjectList);

    List<JSONObject> createJsonObjectListV2(AmazonS3 amazonS3Client, String bucketName, List<JSONObject> jsonObjectList) throws IOException
            , ParseException;

    JSONObject stringToJSONObject(String content) throws ParseException;

    void processDataAwsV2(List<JSONObject> jsonObjectList, Map<String, Long> words, AmazonS3 amazonS3Client,
                       String bucketName) throws IOException, ParseException;

}
