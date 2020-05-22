package com.example.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.ResourceUtils;

public class DataLoader {

    List<String> exclusions = List.of("(", ")", "*", "+", ",", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

    public void loadDataFromFile() throws IOException, ParseException {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        Map<String, Integer> words = new HashMap<String, Integer>();
        File[] listOfFiles = getResourceFolderFiles();
        JSONParser parser = new JSONParser();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                JSONObject jsonObject = (JSONObject) parser.parse(new BufferedReader(new FileReader(listOfFiles[i])));
                jsonObjectList.add(jsonObject);
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        for (JSONObject obj : jsonObjectList) {
            extractDataFromBody((List) obj.get("body_text"), words);
        }
        System.out.println(words.size());
        removeSeveralStuff(words);
        System.out.println(words.size());

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
        words.values().removeIf(value -> value == 1);
        // remove stopwords
        for(String stopWord : Stopwords.stopWordsofwordnet){
            words.keySet().removeIf(key -> stopWord.contains(key));
        }
    }

    private static File[] getResourceFolderFiles() throws IOException {
        File file = ResourceUtils.getFile("classpath:static/");
        return file.listFiles();
    }

}
