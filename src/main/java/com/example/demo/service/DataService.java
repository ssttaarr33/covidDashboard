package com.example.demo.service;

import java.util.Map;

import com.example.demo.utils.aws.DataLoaderFromAWS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    @Autowired
    private DataLoaderFromAWS dataLoaderService;

    public Map<String, Integer> loadData() {
        return dataLoaderService.loadData();
    }

    //
//    public Map<String, Integer> loadData() {
//        try {
//            return new DataLoaderFromLocal().loadData();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
