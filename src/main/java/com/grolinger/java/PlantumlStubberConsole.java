package com.grolinger.java;

import com.grolinger.java.config.PlantumlStubberConfiguration;
import com.grolinger.java.controller.MultiExportController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Simple console runner to generate the (i/p)uml file via command line
 *
 * @author Binh Thai
 * @since 2019-07-17
 */
@Slf4j
@Profile("Console")
@SpringBootApplication
@Configuration
@EnableConfigurationProperties(value = {PlantumlStubberConfiguration.class})
public class PlantumlStubberConsole implements CommandLineRunner {

    @Autowired
    private MultiExportController multiExportController;

    public static void main(String[] args) {
        SpringApplicationBuilder app = new SpringApplicationBuilder(PlantumlStubberConsole.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF);

        app.run(args);
        app.application();
        SpringApplication.exit(app.context(), () -> 0);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("App requires tuples of 2 arguments: diagram <type> (COMP | SEQ) preprocessor (1.2019.6 | 1.2020.7)");
        }
        String diagramType = extractDiagramType(args);
        String preprocessorVersion = extractPreprocessorVersion(args);
        if (CommandArg.COMP.name().equalsIgnoreCase(diagramType)) {
            this.multiExportController.component(null, preprocessorVersion);
        } else if ((CommandArg.SEQ.name().equalsIgnoreCase(diagramType))) {
            this.multiExportController.sequence(null, preprocessorVersion);
        } else {
            throw new IllegalArgumentException("No diagram type matched. Please review the command line arguments. App exiting...");
        }
    }

    /**
     * tries to extract the preprocessor version from 3rd and 4th argument
     *
     * @param args console arguments
     * @return 1.2019.6|1.2020.7
     */
    private String extractPreprocessorVersion(String[] args) {
        String preprocessorVersion = "1.2020.7";
        if (args.length >= 4) {
            if ("preprocessor".equalsIgnoreCase(args[2])
                    && args[3].startsWith("1.")) {
                //todo search args
                preprocessorVersion = args[3];
                log.info("Using preprocessor version {}", preprocessorVersion);
            } else {
                log.info("Using default preprocessor version {}", preprocessorVersion);
            }
        }
        return preprocessorVersion;
    }

    /**
     * Tries to extract the diagramType from the console 1st and 2nd argument
     *
     * @param args console argument <i>diagram</i> COMP|SEQ
     * @return COMP|SEQ|null
     */
    private String extractDiagramType(String[] args) {
        String diagramType = null;
        if (args.length >= 2 && "diagram".equalsIgnoreCase(args[0])) {
            diagramType = args[1];
        }
        log.info("Using diagram {}", diagramType);
        return diagramType;
    }

    private enum CommandArg {
        COMP,
        SEQ
    }
}