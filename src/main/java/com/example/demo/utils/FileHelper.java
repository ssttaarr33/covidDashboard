package com.example.demo.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface FileHelper {

    void removeSeveralStuffV2(Map<String, Long> words);

    Map<String, Long> parseJsonObjectsV2(List<JSONObject> jsonObjectList);

    List<JSONObject> createJsonObjectList(List<Path> listOfFiles, List<JSONObject> jsonObjectList) throws IOException, ParseException;

    JSONObject fileToJSONObject(File file) throws IOException, ParseException;

    List<Path> getResourceFolderFiles() throws IOException;

    void processData(List<JSONObject> jsonObjectList, Map<String, Long> words, List<Path> listOfFiles) throws IOException, ParseException;
}
