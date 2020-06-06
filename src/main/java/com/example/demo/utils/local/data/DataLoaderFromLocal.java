package com.example.demo.utils.local.data;

import java.io.IOException;
import java.util.Map;

import com.example.demo.utils.DataLoader;
import com.example.demo.utils.DataLoaderInterface;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataLoaderFromLocal extends DataLoader implements DataLoaderInterface {

    @Override
    @Timed(description = "Time to load files from jar", value = "dataloader.load")
    public Map<String, Long> loadData() throws IOException, ParseException {

        listOfFiles = helper.getResourceFolderFiles();
        words = helper.processData(jsonObjectList, words, listOfFiles);

        return words;
    }
}
