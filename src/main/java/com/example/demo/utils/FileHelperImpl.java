package com.example.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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
    @Timed(description = "Time to load files from jar", value = "dataloader.load")
    public void processData(List<JSONObject> jsonObjectList, Map<String, Integer> words, List<Path> listOfFiles) throws IOException, ParseException {
        createJsonObjectList(listOfFiles, jsonObjectList);
        parseJsonObjects(jsonObjectList, words);
        removeSeveralStuff(words);
    }

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
            if (obj != null) {
                extractDataFromBody((List) obj.get(BODY_KEY), words);
            }
        }
    }

    public void createJsonObjectList(List<Path> listOfFiles, List<JSONObject> jsonObjectList) throws IOException, ParseException {
        for (int i = 0; i < listOfFiles.size(); i++) {
            jsonObjectList.add(fileToJSONObject(listOfFiles.get(i).toFile()));
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
    public JSONObject fileToJSONObject(File file) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) getParser().parse(fileReader);
        } catch (ParseException e) {
        } finally {
            fileReader.close();
        }
        return jsonObject;
    }

    @Override
    public List<Path> getResourceFolderFiles() throws IOException {
        String relativePath = LOCAL_RESOURCE_FILE_LOCATION;
        Path staticPath = Paths.get(relativePath);
        return Files.list(staticPath).collect(Collectors.toList());
    }
}
