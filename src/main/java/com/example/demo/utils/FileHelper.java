package com.example.demo.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface FileHelper {
    void extractDataFromBody(List<JSONObject> bodyLines, Map<String, Integer> words);

    void removeSeveralStuff(Map<String, Integer> words);

    void parseJsonObjects(List<JSONObject> jsonObjectList, Map<String, Integer> words);

    void createJsonObjectList(File[] listOfFiles, List<JSONObject> jsonObjectList) throws IOException, ParseException;

    JSONObject fileToJSONObject(File file) throws IOException, ParseException;

    JSONObject stringToJSONObject(String content) throws ParseException;

    File[] getResourceFolderFiles() throws IOException;

    void processData(List<JSONObject> jsonObjectList, Map<String, Integer> words, File[] listOfFiles) throws IOException, ParseException;
}