package com.example.demo.utils;

import java.io.IOException;
import java.util.Map;

import org.json.simple.parser.ParseException;

public interface DataLoaderInterface {
    Map<String, Long> loadData() throws IOException, ParseException;
}
