package com.example.demo.api;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.model.RestResponse;
import com.example.demo.service.DataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

import static com.example.demo.model.CommonErrorCode.ERROR_INTERNAL_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "${app.endpoint.api}", produces = APPLICATION_JSON_VALUE)
public class DataController {

    @Autowired
    private final DataService dataService;

    @CrossOrigin(origins = "${app.allowed.origin.localhost}")
    @GetMapping("${app.endpoint.get.data}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Get data for dashboard")
    public RestResponse<Map<String, Long>> getDataForDashboard() {
        log.info("Get data for dashboard");
        try {
            return RestResponse.ok(dataService.loadData());
        } catch (IOException e) {
            e.printStackTrace();
            return RestResponse.fail(ERROR_INTERNAL_ERROR, "FAIL: " + e.getMessage(), new HashMap<>());
        } catch (ParseException e) {
            e.printStackTrace();
            return RestResponse.fail(ERROR_INTERNAL_ERROR, "FAIL: " + e.getMessage(), new HashMap<>());
        }
    }
}
