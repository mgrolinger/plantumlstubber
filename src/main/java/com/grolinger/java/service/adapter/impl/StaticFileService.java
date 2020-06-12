package com.grolinger.java.service.adapter.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import static com.grolinger.java.controller.templatemodel.Constants.SLASH;

/**
 * Exports different static files baked into the release to make the generated repository functional
 * (such as common.iuml or color definitions)
 */
@Slf4j
public class StaticFileService {
    // FIXME
    private static final String GLOBAL_FILE_EXPORT_PATH = System.getProperty("user.dir") + SLASH.getValue() + "target" + SLASH.getValue();

    private StaticFileService() {
        //hide
    }

    /**
     * Copies the file from the resource folder in the jar to the specified output folder
     *
     * @param folder          name of the subfolder from where the file is read
     * @param currentFileName which file is processed
     */
    public static void copyFile(final String folder, final String currentFileName) {
        try {
            final String completeFileName = folder + currentFileName;
            ClassPathResource classPathResource = new ClassPathResource(completeFileName);
            if (classPathResource.exists()) {
                // Get the input stream of the file
                InputStream in = new ClassPathResource(completeFileName).getInputStream();
                // Copy it directly to the output
                Files.copy(in,
                        new File(GLOBAL_FILE_EXPORT_PATH + currentFileName).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } else {
                log.warn("File {} doesn't exist in resource folder. Skipping it.", completeFileName);
            }
        } catch (IOException e) {
            // do nothing
            log.error("An exception occurred: {}", e.getMessage());
        }
    }

    public static void readFile(final String inputFileName, final Path outputFile) {
        try (Stream<String> stream = Files.lines(Paths.get(inputFileName))) {
            log.info("Feature not yet implemented; Read file:{} and output it to: {}", inputFileName, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
