package com.grolinger.java.service.adapter.exportdata.impl;

import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.adapter.exportdata.LocalStaticAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.grolinger.java.controller.templatemodel.Constants.SLASH;

/**
 * Exports different static files baked into the release to make the generated repository functional
 * (such as common.iuml or color definitions)
 */
@Component
@Slf4j
public class LocalStaticAdapterImpl implements LocalStaticAdapter {
    // FIXME move to config class
    public static final String STATIC_INCLUDES = "static_includes";
    private static final String COMMON_FILE = "common" + SLASH.getValue() + "common.iuml";
    private static final String GLOBAL_FILE_EXPORT_PATH = System.getProperty("user.dir") + SLASH.getValue() + "target" + SLASH.getValue();
    private static final String COMMON_PATH = "common" + SLASH.getValue();

    private LocalStaticAdapterImpl() {
        //hide
    }

    /**
     * Copies the file from the resource folder in the jar to the specified output folder
     *
     * @param folder          name of the subfolder from where the file is read
     * @param currentFileName which file is processed
     */
    @Override
    public void copyFile(final String folder, final String currentFileName) {
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

    @Override
    public void exportTemplate(){
        copyFile("","_template_newApplication.yaml");
    }

    @Override
    public void readFile(final String inputFileName, final Path outputFile) {
        try (Stream<String> stream = Files.lines(Paths.get(inputFileName))) {
            log.info("Feature not yet implemented; Read file:{} and output it to: {}", inputFileName, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void writeDefaultCommonFile(final String basePath, final DiagramType diagramType) throws IOException {
        List<String> filesToExport;
        //basePath already contains the diagram type
        final String pathToExport = GLOBAL_FILE_EXPORT_PATH + basePath;
        Files.createDirectories(Paths.get(pathToExport + COMMON_PATH));
        log.info("Exporting base path {}", pathToExport);
        if (DiagramType.COMPONENT_V1_2019_6_DIAGRAM_BASE.equals(diagramType) ||
                DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE.equals(diagramType)) {
            filesToExport = Arrays.asList(
                    COMMON_FILE,
                    COMMON_PATH + "domain_legend.iuml",
                    COMMON_PATH + "devicons" + SLASH.getValue() + "springBoot.puml",
                    COMMON_PATH + "devicons" + SLASH.getValue() + "salesforce.puml",
                    COMMON_PATH + "devicons" + SLASH.getValue() + "sap.puml",
                    COMMON_PATH + "devicons" + SLASH.getValue() + "solr.puml",
                    COMMON_PATH + "devicons" + SLASH.getValue() + "tibco.puml",
                    "Readme.puml");
            log.info("Write some common files: {}", filesToExport);
            // FIXME: better solution than hard-coding here
            Files.createDirectories(Paths.get(pathToExport + COMMON_PATH + "devicons"));
        } else {
            filesToExport = Arrays.asList(
                    COMMON_FILE,
                    COMMON_PATH + "box.iuml",
                    COMMON_PATH + "http_statuscodes.iuml");
        }

        for (String currentFileName : filesToExport) {
            copyFile(STATIC_INCLUDES + SLASH.getValue(),
                    diagramType.getBasePath() + currentFileName);
        }
    }

    @Override
    public void writeDefaultSkinFiles() throws IOException {
        final String subdir = "skin" + SLASH.getValue();
        List<String> filesToExport = Arrays.asList(
                subdir + "ci_company_colors.iuml",
                subdir + "default.skin",
                subdir + "darcula.skin");
        log.info("Write some skin files: {}", filesToExport);
        Files.createDirectories(Paths.get(GLOBAL_FILE_EXPORT_PATH + subdir));
        //skin
        for (String currentFileName : filesToExport) {
            copyFile(STATIC_INCLUDES + SLASH.getValue(), currentFileName);
        }
    }
}
