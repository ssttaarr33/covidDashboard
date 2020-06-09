package com.example.demo.api;


import java.util.Map;

import com.example.demo.model.RestResponse;
import com.example.demo.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(path = "/data-api", produces = APPLICATION_JSON_VALUE)
public class DataController {

    @Autowired
    DataService dataService;

    @CrossOrigin(origins = "http://localhost")
    @GetMapping(value = "/dashboard")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Get data for dashboard")
    public RestResponse<Map<String, Integer>> getBonusState() {
        log.info("Get data for dashboard");
        return RestResponse.ok(dataService.loadData());
    }
}
