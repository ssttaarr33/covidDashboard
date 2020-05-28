package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties("app")
@Configuration
public class ApplicationProperties {

    private AwsServices awsServices;

    @Data
    public static class AwsServices{

        private String bucketName;
    }
}
