package com.example.demo.service;

import java.io.IOException;
import java.util.Map;

import com.example.demo.utils.local.data.DataLoaderFromLocal;
import lombok.AllArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DataService {

    private DataLoaderFromLocal dataLoader;

    public Map<String, Long> loadData() throws IOException, ParseException {
        return dataLoader.loadData();
    }
}
