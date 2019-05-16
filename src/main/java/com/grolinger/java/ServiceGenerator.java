package com.grolinger.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.grolinger.java"})
public class ServiceGenerator {
    public static void main(String[] args) {
        SpringApplication.run(ServiceGenerator.class, args);
    }

}
