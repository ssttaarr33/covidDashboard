package com.example.demo.utils.local.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.example.demo.utils.DataLoader;
import com.example.demo.utils.DataLoaderInterface;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataLoaderFromLocal extends DataLoader implements DataLoaderInterface {

    @Override
    public Map<String, Integer> loadData() throws IOException, ParseException {

        List<Path> listOfFiles = helper.getResourceFolderFiles();
        helper.processData(jsonObjectList, words, listOfFiles);

        return words;
    }
}
