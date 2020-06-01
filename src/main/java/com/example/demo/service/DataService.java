package com.example.demo.service;

import java.util.Map;

import com.example.demo.utils.aws.DataLoaderFromAWS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    @Autowired
    private DataLoaderFromAWS dataLoaderService;

    public Map<String, Long> loadData() {
        return dataLoaderService.loadData();
    }

}
