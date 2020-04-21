package com.grolinger.java;

import com.grolinger.java.config.PlantumlStubberConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine;


@CommandLine.Command(
        name = "PlantumlStubber",
        description = "Generator for plantUML sequence and component repositories."
)
@Configuration
@EnableConfigurationProperties(value = {PlantumlStubberConfiguration.class})
@SpringBootApplication(scanBasePackages = {"com.grolinger.java"})
public class PlantumlStubber {
    private static final Logger logger = LoggerFactory.getLogger(PlantumlStubber.class);
    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port on which the application will provide the swagger ui.")
    private static final String port = "19191";
    @CommandLine.Option(
            names = {"-i", "--inputDirectory"},
            description = "Directory from which the configuration yaml files will be read.")
    private static String inputDirectory;
    @CommandLine.Option(
            names = {"-o", "--outputDirectory"},
            description = "Directory to which the resulting directories and files will be written.")
    private static String outputDirectory;

    public static void main(String[] args) {


//"Defines the input directory, where *.yaml files with the application definitions are located."
        //TODO JCommander etc
        logger.warn("P {}, I {}, O {}", port, inputDirectory, outputDirectory);
        SpringApplication.run(PlantumlStubber.class, args);
        logger.info("Swagger accessible via http://localhost:{}/swagger-ui.html#/", port);
    }

}
