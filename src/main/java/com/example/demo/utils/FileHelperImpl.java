package com.example.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.example.demo.utils.aws.AWSRepositoryImpl;
import com.example.demo.utils.local.data.Stopwords;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Slf4j
public class FileHelperImpl implements FileHelper {

    AWSRepositoryImpl awsRepository = new AWSRepositoryImpl();

    JSONParser parser = new JSONParser();
    private static final String BODY_KEY = "body_text";

    @Override
    public void removeSeveralStuffV2(Map<String, Long> words) {
        Arrays.stream(Regex.values()).forEach(regex -> words.keySet().removeIf(key -> key.matches(regex.getRegex())));
        // remove single occurrences
        words.values().removeIf(value -> value < 30);
        // remove stopwords
        removeStopWordsV2(words);
    }

    @Override
    public Map<String, Long> parseJsonObjectsV2(List<JSONObject> jsonObjectList) {

        return jsonObjectList.stream()
                             .map(jsonObject -> jsonObject.get(BODY_KEY))
                             .map(line -> ((JSONObject) line).get("text").toString().split(Regex.SPACE.getRegex()))
                             .flatMap(stringArray -> Arrays.stream(stringArray))
                             .map(word -> word.replaceAll(Regex.PUNCTUATION.getRegex(), ""))
                             .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    }

    private void removeStopWordsV2(Map<String, Long> words) {
        Arrays.stream(Stopwords.stopWordsofwordnet).map(stopWord -> words.keySet().removeIf(key -> stopWord.contains(key)));
    }

    private JSONParser getParser() {
        return parser;
    }

    @Override
    public JSONObject stringToJSONObject(String content) throws ParseException {
        return (JSONObject) getParser().parse(content);
    }

    @Override
    public void processDataAwsV2(List<JSONObject> jsonObjectList, Map<String, Long> words, AmazonS3 amazonS3Client,
                                 String bucketName) {
        jsonObjectList = createJsonObjectListV2(amazonS3Client, bucketName, jsonObjectList);
        words = parseJsonObjectsV2(jsonObjectList);
        removeSeveralStuffV2(words);
    }

    @Override
    public List<JSONObject> createJsonObjectListV2(AmazonS3 amazonS3Client, String bucketName, List<JSONObject> jsonObjectList) {
        long start = System.currentTimeMillis();
        Iterable<S3ObjectSummary> objectSummaries = S3Objects.inBucket(amazonS3Client, bucketName);
        Stream<S3ObjectSummary> objectSummaryStream = StreamSupport.stream(objectSummaries.spliterator(), true);
        jsonObjectList = objectSummaryStream.map(s3ObjectSummary -> awsRepository.downloadFileFromS3Bucket(s3ObjectSummary.getKey(), bucketName,
                                                                                                           amazonS3Client))
                                            .map(Either.liftWithValue(object -> IOUtils.toString(object.getObjectContent())))
                                            .filter(option -> option.isRight())
                                            .map(Either.liftWithValue(fileContent -> stringToJSONObject(fileContent.getRight().toString())))
                                            .filter(option -> option.isRight())
                                            .map(option -> (JSONObject) option.getRight())
                                            .collect(Collectors.toList());

        log.info("Time: {} seconds", (System.currentTimeMillis() - start) / 1000);

        return jsonObjectList;
    }

}
