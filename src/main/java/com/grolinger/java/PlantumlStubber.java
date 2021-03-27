package com.grolinger.java;

import com.grolinger.java.config.PlantumlStubberConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(value = {PlantumlStubberConfiguration.class})
@SpringBootApplication(scanBasePackages = {"com.grolinger.java"})
public class PlantumlStubber {
    private static final String port = "19191";
    private static String inputDirectory;
    private static String outputDirectory;

    public static void main(String[] args) {
        //TODO JCommander etc
        SpringApplication.run(PlantumlStubber.class, args);
        log.info("GUI accessible via http://localhost:{}/swagger-ui.html#/", port);
    }

}
