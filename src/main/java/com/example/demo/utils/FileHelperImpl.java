package com.example.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.demo.utils.local.data.Stopwords;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Slf4j
public class FileHelperImpl implements FileHelper {

    JSONParser parser = new JSONParser();
    private static final String LOCAL_RESOURCE_FILE_LOCATION = "/opt/static";
    private static final String BODY_KEY = "body_text";

    @Override
    @Timed(description = "Time to load files from jar", value="dataloader.load")
    public void processData(List<JSONObject> jsonObjectList, Map<String, Long> words, List<Path> listOfFiles) throws IOException, ParseException {
        jsonObjectList = createJsonObjectList(listOfFiles, jsonObjectList);
        words = parseJsonObjectsV2(jsonObjectList);
        removeSeveralStuffV2(words);
    }


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

    @Override
    public List<JSONObject> createJsonObjectList(List<Path> listOfFiles, List<JSONObject> jsonObjectList) throws IOException, ParseException {
        return listOfFiles.stream()
                     .map(Either.liftWithValue(path -> fileToJSONObject(path.toFile())))
                     .filter(option -> option.isRight())
                     .map(option -> (JSONObject) option.getRight())
                     .collect(Collectors.toList());

    }

    private void removeStopWordsV2(Map<String, Long> words) {
        Arrays.stream(Stopwords.stopWordsofwordnet).map(stopWord -> words.keySet().removeIf(key -> stopWord.contains(key)));
    }

    private JSONParser getParser() {
        return parser;
    }

    @Override
    public JSONObject fileToJSONObject(File file) throws IOException, ParseException {
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        JSONObject jsonObject = (JSONObject) getParser().parse(fileReader);
        fileReader.close();
        return jsonObject;
    }

    @Override
    public List<Path> getResourceFolderFiles() throws IOException {
        String relativePath = LOCAL_RESOURCE_FILE_LOCATION;
        Path staticPath = Paths.get(relativePath);
        return Files.list(staticPath).collect(Collectors.toList());
    }
}
