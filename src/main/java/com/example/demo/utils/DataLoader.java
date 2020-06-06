package com.example.demo.utils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public class DataLoader {

    public FileHelperImpl helper = new FileHelperImpl();
    public List<JSONObject> jsonObjectList = new ArrayList<>();
    public Map<String, Long> words = new HashMap<>();
    public List<Path> listOfFiles = new ArrayList<>();
}
