package com.example.demo.service;

import java.io.IOException;
import java.util.Map;

import com.example.demo.utils.DataLoader;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    public Map<String, Integer> loadData() {
        try {
            return new DataLoader().loadDataFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
