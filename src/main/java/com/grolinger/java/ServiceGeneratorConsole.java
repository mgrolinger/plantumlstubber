package com.grolinger.java;

import com.grolinger.java.config.PlantumlStubberConfiguration;
import com.grolinger.java.controller.MultiExportController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Simple console runner to generate the iuml file via command line, e.g. with Gradle
 * @author Binh Thai
 * @since 2019-07-17
 */
@Profile("Console")
@SpringBootApplication
@Configuration
@EnableConfigurationProperties(value = {PlantumlStubberConfiguration.class})
public class ServiceGeneratorConsole implements CommandLineRunner {

    private enum CommandArg {
        COMP,
        SEQ
    }

    @Autowired
    private MultiExportController multiExportController;

    public static void main(String[] args) {
        SpringApplicationBuilder app = new SpringApplicationBuilder(ServiceGeneratorConsole.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF);

        app.run(args);
        app.application().exit(app.context(), () -> 0);
    }

    @Override
    public void run(String... args) throws Exception {
        //FIXME this doesn't work anymore
        if (args.length != 2) {
            throw new IllegalArgumentException("App requires 2 arguments: diagram type (COM | SEQ)");
        }
        String diagramType = args[0];

        if (diagramType.equalsIgnoreCase(CommandArg.COMP.name())) {
            this.multiExportController.component(null);
        } else if (diagramType.equalsIgnoreCase(CommandArg.SEQ.name())) {
            this.multiExportController.sequence(null);
        } else {
            throw new IllegalArgumentException("No diagramme type matched. Please review the command line arguments. App exiting...");
        }
    }
}
