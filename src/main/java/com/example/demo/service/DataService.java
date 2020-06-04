package com.example.demo.service;

import java.io.IOException;
import java.util.Map;

import com.example.demo.utils.local.data.DataLoaderFromLocal;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    @Autowired
    private DataLoaderFromLocal dataLoaderService;

    public Map<String, Integer> loadData() throws IOException, ParseException {
        return dataLoaderService.loadData();
    }
}
