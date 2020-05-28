package com.example.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.example.demo.utils.aws.AWSRepositoryImpl;
import com.example.demo.utils.local.data.Stopwords;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.ResourceUtils;

@Slf4j
public class FileHelperImpl implements FileHelper {

    AWSRepositoryImpl awsRepository = new AWSRepositoryImpl();

    JSONParser parser = new JSONParser();
    private static final String LOCAL_RESOURCE_FILE_LOCATION = "classpath:static/";
    private static final String BODY_KEY = "body_text";

    @Override
    public void extractDataFromBody(List<JSONObject> bodyLines, Map<String, Integer> words) {
        for (JSONObject line : bodyLines) {
            String[] splitted = line.get("text").toString().split(Regex.SPACE.getRegex());
            for (String word : splitted) {
                word = word.replaceAll(Regex.PUNCTUATION.getRegex(), "");
                if (words.containsKey(word)) {
                    words.put(word, words.get(word) + 1);
                } else {
                    words.put(word, 1);
                }
            }
        }
    }

    @Override
    public void removeSeveralStuff(Map<String, Integer> words) {
        // remove single characters
        words.keySet().removeIf(key -> key.matches(Regex.SINGLE_CHARACTER.getRegex()));
        // remove single alphanumeric
        words.keySet().removeIf(key -> key.matches(Regex.ALPHANUMERIC.getRegex()));
        // remove single digit
        words.keySet().removeIf(key -> key.matches(Regex.SINGLE_DIGIT.getRegex()));
        // remove double digit
        words.keySet().removeIf(key -> key.matches(Regex.DOUBLE_DIGIT.getRegex()));
        // remove triple digit
        words.keySet().removeIf(key -> key.matches(Regex.TRIPLE_DIGIT.getRegex()));
        // remove quadruple digit
        words.keySet().removeIf(key -> key.matches(Regex.QUADRUPLE_DIGIT.getRegex()));
        // remove single occurrences
        words.values().removeIf(value -> value < 30);
        // remove stopwords
        removeStopWords(words);
    }

    @Override
    public void parseJsonObjects(List<JSONObject> jsonObjectList, Map<String, Integer> words) {
        for (JSONObject obj : jsonObjectList) {
            extractDataFromBody((List) obj.get(BODY_KEY), words);
        }
    }

    public void createJsonObjectList(File[] listOfFiles, List<JSONObject> jsonObjectList) throws IOException, ParseException {
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                jsonObjectList.add(fileToJSONObject(listOfFiles[i]));
            } else if (listOfFiles[i].isDirectory()) {
                log.info("Directory " + listOfFiles[i].getName());
            }
        }
    }

    private void removeStopWords(Map<String, Integer> words) {
        for (String stopWord : Stopwords.stopWordsofwordnet) {
            words.keySet().removeIf(key -> stopWord.contains(key));
        }
    }

    private JSONParser getParser() {
        return parser;
    }

    @Override
    public JSONObject fileToJSONObject(File file) throws IOException, ParseException {
        return (JSONObject) getParser().parse(new BufferedReader(new FileReader(file)));
    }

    @Override
    public JSONObject stringToJSONObject(String content) throws ParseException {
        return (JSONObject) getParser().parse(content);
    }

    @Override
    public File[] getResourceFolderFiles() throws IOException {
        File file = ResourceUtils.getFile(LOCAL_RESOURCE_FILE_LOCATION);
        return file.listFiles();
    }

    @Override
    public void processData(List<JSONObject> jsonObjectList, Map<String, Integer> words, File[] listOfFiles) throws IOException, ParseException {
        createJsonObjectList(listOfFiles, jsonObjectList);
        parseJsonObjects(jsonObjectList, words);
        removeSeveralStuff(words);
    }

    @Override
    public void processData(List<JSONObject> jsonObjectList, Map<String, Integer> words, AmazonS3 amazonS3Client, ListObjectsV2Request req,
                            String bucketName) throws IOException, ParseException {
        createJsonObjectList(amazonS3Client, req, bucketName, jsonObjectList);
        parseJsonObjects(jsonObjectList, words);
        removeSeveralStuff(words);
    }

    @Override
    public void createJsonObjectList(AmazonS3 amazonS3Client, ListObjectsV2Request req, String bucketName, List<JSONObject> jsonObjectList) throws IOException
            , ParseException {
        ListObjectsV2Result result;
        do {
            long start = System.currentTimeMillis();
            result = amazonS3Client.listObjectsV2(req);
            S3Object object;
            String fileContent;
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
//                log.info(" - %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
                object = awsRepository.downloadFileFromS3Bucket(objectSummary.getKey(), bucketName, amazonS3Client);
                fileContent = IOUtils.toString(object.getObjectContent());
                jsonObjectList.add(stringToJSONObject(fileContent));
            }
            // If there are more than maxKeys keys in the bucket, get a continuation token
            // and list the next objects.
            String token = result.getNextContinuationToken();
//            log.info("Next Continuation Token: " + token);
            req.setContinuationToken(token);
            log.info("Time: {} seconds", (System.currentTimeMillis() - start)/1000);
        } while (result.isTruncated());
    }


}
