package com.example.demo.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import com.example.demo.utils.DataLoader;
import lombok.AllArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DataService {

    private DataLoader dataLoader;

    public Map<String, Integer> loadData() {
        try {
            return dataLoader.loadDataFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
