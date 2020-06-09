package com.example.demo.utils;

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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.example.demo.utils.local.data.Stopwords;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

@Slf4j
public class FileHelperImpl implements FileHelper {

    ObjectMapper objectMapper = new ObjectMapper();
    private static final String LOCAL_RESOURCE_FILE_LOCATION = "/opt/static";
    private static final String BODY_KEY = "body_text";
    private static final String TEXT_KEY = "text";
    private List<String> removableWords = Arrays.asList(Stopwords.stopWordsofwordnet);

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
                          .parallel()
                          .map(Either.liftWithValue(path -> fileToJSONObject(path.toFile())))
                          .filter(option -> option.isRight())
                          .map(option -> (JsonNode) option.getRight())
                          .map(jsonObject -> jsonObject.get(BODY_KEY))
                          .flatMap(array -> StreamSupport.stream(array.spliterator(), true)
                                                         .map(object -> object.get(TEXT_KEY).toString()))
                          .map(text -> text.replaceAll(Regex.PUNCTUATION.getRegex(), ""))
                          .map(text -> text.split(Regex.SPACE.getRegex()))
                          .flatMap(stringArray -> Arrays.stream(stringArray))
                          .filter(word -> !(word.isEmpty())
                                  && !(word.length() < 4)
                                  && !word.matches(Regex.SINGLE_CHARACTER.getRegex())
                                  && !word.matches(Regex.ALPHANUMERIC.getRegex())
                                  && !word.matches(Regex.SINGLE_DIGIT.getRegex())
                                  && !word.matches(Regex.DOUBLE_DIGIT.getRegex())
                                  && !word.matches(Regex.TRIPLE_DIGIT.getRegex())
                                  && !word.matches(Regex.QUADRUPLE_DIGIT.getRegex())
                                  && !removableWords.contains(word)
                          )
                          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    @Override
    public JsonNode fileToJSONObject(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            return objectMapper.readTree(fileReader);
        }
    }

    @Override
    public List<Path> getResourceFolderFiles() throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(LOCAL_RESOURCE_FILE_LOCATION))) {
            return walk.filter(Files::isRegularFile).collect(Collectors.toList());
        }
    }
}
