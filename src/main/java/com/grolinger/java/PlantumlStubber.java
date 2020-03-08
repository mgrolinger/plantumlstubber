package com.grolinger.java;

import com.grolinger.java.config.PlantumlStubberConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {PlantumlStubberConfiguration.class})
@SpringBootApplication(scanBasePackages = {"com.grolinger.java"})
public class PlantumlStubber {
    private static final Logger logger = LoggerFactory.getLogger(PlantumlStubber.class);

    public static void main(String[] args) {

        //TODO JCommander etc
        SpringApplication.run(PlantumlStubber.class, args);
        logger.info("Swagger accessible via http://localhost:19191/swagger-ui.html#/");
    }

}
