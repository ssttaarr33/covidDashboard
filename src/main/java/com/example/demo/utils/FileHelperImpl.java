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
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Slf4j
public class FileHelperImpl implements FileHelper {

    JSONParser parser = new JSONParser();
    private static final String LOCAL_RESOURCE_FILE_LOCATION = "/opt/static";
    private static final String BODY_KEY = "body_text";
    private static final String TEXT_KEY = "text";

    @Override
    public Map<String, Long> processData(List<JSONObject> jsonObjectList, Map<String, Long> words, List<Path> listOfFiles) throws IOException, ParseException {
        words = createJsonObjectList(listOfFiles, jsonObjectList);
        ;
        removeSeveralStuffV2(words);
        return words;
    }


    @Override
    public void removeSeveralStuffV2(Map<String, Long> words) {
        // remove single occurrences
        words.values().removeIf(value -> value < 30);
    }

    @Override
    public Map<String, Long> createJsonObjectList(List<Path> listOfFiles, List<JSONObject> jsonObjectList) {
        return listOfFiles.stream()
                          .map(Either.liftWithValue(path -> fileToJSONObject(path.toFile())))
                          .filter(option -> option.isRight())
                          .map(option -> (JSONObject) option.getRight())
                          .map(jsonObject -> jsonObject.get(BODY_KEY))
                          .map(array -> ((JSONArray) array).parallelStream()
                                                           .map(object -> ((JSONObject) object).get(TEXT_KEY).toString())
                                                           .collect(Collectors.toList()))
                          .flatMap(stringArray -> ((List<String>) stringArray).stream())
                          .map(text -> text.split(Regex.SPACE.getRegex()))
                          .flatMap(stringArray -> Arrays.stream(stringArray))
                          .map(word -> word.replaceAll(Regex.PUNCTUATION.getRegex(), ""))
                          .filter(word -> !(word.isEmpty())
                                  && !(word.length() < 4)
                                  && !word.matches(Regex.SINGLE_CHARACTER.getRegex())
                                  && !word.matches(Regex.ALPHANUMERIC.getRegex())
                                  && !word.matches(Regex.SINGLE_DIGIT.getRegex())
                                  && !word.matches(Regex.DOUBLE_DIGIT.getRegex())
                                  && !word.matches(Regex.TRIPLE_DIGIT.getRegex())
                                  && !word.matches(Regex.QUADRUPLE_DIGIT.getRegex())
                                  && !Arrays.asList(Stopwords.stopWordsofwordnet).contains(word)
                          )
                          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private JSONParser getParser() {
        return parser;
    }

    @Override
    public JSONObject fileToJSONObject(File file) throws IOException, ParseException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        JSONObject jsonObject = (JSONObject) getParser().parse(bufferedReader);
        fileReader.close();
        bufferedReader.close();
        return jsonObject;
    }

    @Override
    public List<Path> getResourceFolderFiles() throws IOException {
        String relativePath = LOCAL_RESOURCE_FILE_LOCATION;
        Path staticPath = Paths.get(relativePath);
        return Files.list(staticPath).collect(Collectors.toList());
    }
}
