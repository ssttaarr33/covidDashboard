package com.example.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DataLoader {

    private DefaultResourceLoader resourceLoader;

    @Timed(description = "Time to load files from jar", value="dataloader.load")
    public Map<String, Integer> loadDataFromFile() throws IOException, ParseException, URISyntaxException {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        Map<String, Integer> words = new HashMap<>();
        List<Path> listOfFiles = getResourceFolderFiles();
        JSONParser parser = new JSONParser();
        for (int i = 0; i < listOfFiles.size(); i++) {
           File targetReader = listOfFiles.get(i).toFile();
           BufferedReader fileReader = new BufferedReader(new FileReader(targetReader));
           JSONObject jsonObject = (JSONObject) parser.parse(fileReader);
           fileReader.close();
           jsonObjectList.add(jsonObject);
        }
        for (JSONObject obj : jsonObjectList) {
            extractDataFromBody((List) obj.get("body_text"), words);
        }
        removeSeveralStuff(words);

        return words;
    }

    private void extractDataFromBody(List<JSONObject> bodyLines, Map<String, Integer> words) {
        for (JSONObject line : bodyLines) {
            String[] splitted = line.get("text").toString().split("\\s+");
            for (String word : splitted) {
                word = word.replaceAll("[^A-Za-z0-9 -]", "");
                if (words.containsKey(word)) {
                    words.put(word, words.get(word) + 1);
                } else {
                    words.put(word, 1);
                }
            }
        }
    }

    private void removeSeveralStuff(Map<String, Integer> words) {
        // remove single characters
        words.keySet().removeIf(key -> key.matches("(^|\\s+)[a-zA-Z](\\s+|$)"));
        // remove single alphanumeric
        words.keySet().removeIf(key -> key.matches("[^a-zA-Z0-9 -]"));
        // remove single numeric
        words.keySet().removeIf(key -> key.matches("[0-9]"));
        // remove double numeric
        words.keySet().removeIf(key -> key.matches("^[0-9]{2}$"));
        // remove triple numeric
        words.keySet().removeIf(key -> key.matches("^[0-9]{3}$"));
        // remove quadruple numeric
        words.keySet().removeIf(key -> key.matches("^[0-9]{4}$"));
        // remove single occurences
        words.values().removeIf(value -> value < 30);
        // remove stopwords
        for(String stopWord : Stopwords.stopWordsofwordnet){
            words.keySet().removeIf(key -> stopWord.contains(key));
        }
    }

    private List<Path> getResourceFolderFiles() throws IOException {
        String relativePath = "/opt/static";
        Path staticPath = Paths.get(relativePath);
        return Files.list(staticPath).collect(Collectors.toList());
    }

}
