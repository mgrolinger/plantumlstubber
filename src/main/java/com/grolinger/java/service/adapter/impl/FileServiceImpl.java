package com.grolinger.java.service.adapter.impl;

import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.adapter.FileService;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.exportdata.ComponentFile;
import com.grolinger.java.service.data.exportdata.ExampleFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.grolinger.java.controller.templatemodel.Constants.PATH_SEPARATOR;
import static com.grolinger.java.controller.templatemodel.Constants.SLASH;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    public static final String FILE_TYPE_IUML = ".iuml";
    public static final String STATIC_INCLUDES = "static_includes";
    private static final String GLOBAL_FILE_EXPORT_PATH = System.getProperty("user.dir") + SLASH.getValue() + "target" + SLASH.getValue();
    private static final String COMMON_PATH = "common" + SLASH.getValue();
    private static final String COMMON_FILE = "common" + SLASH.getValue() + "common.iuml";
    private static final String COMPONENT_DEFINITION_FILE = "component_definition.iuml";
    private static final String PARTICIPANT_DEFINITION_FILE = "participant_definition.iuml";
    private static final String EXAMPLE_FILE_SUFFIX = "_example.puml";
    private final SpringTemplateEngine templateEngine;

    @Autowired
    private FileServiceImpl(SpringTemplateEngine templateEngine) {
        log.info("Configure base folder to read yaml files: {}", GLOBAL_FILE_EXPORT_PATH);
        this.templateEngine = templateEngine;
    }


    @Override
    public String createServiceDirectory(final String basePath, final ApplicationDefinition applicationDefinition, final ServiceDefinition serviceDefinition) throws IOException {
        String path;
        String applicationPart = "";
        if (null != applicationDefinition.getPath()) {
            applicationPart = applicationDefinition.getPath() + PATH_SEPARATOR.getValue();
        }
        String servicePart = "";
        if (null != serviceDefinition.getPath()) {
            servicePart = serviceDefinition.getPath();
        }
        path = GLOBAL_FILE_EXPORT_PATH + basePath + applicationPart + servicePart;
        Files.createDirectories(Paths.get(path));
        return path;
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
            // FIXME: better solution than hardcoding here
            Files.createDirectories(Paths.get(pathToExport + COMMON_PATH + "devicons"));
        } else {
            filesToExport = Arrays.asList(
                    COMMON_FILE,
                    COMMON_PATH + "bipro_messageIds.iuml",
                    COMMON_PATH + "http_statuscodes.iuml");
        }

        for (String currentFileName : filesToExport) {
            StaticFileService.copyFile(STATIC_INCLUDES + SLASH.getValue(),
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
            StaticFileService.copyFile(STATIC_INCLUDES + SLASH.getValue(), currentFileName);
        }
    }


    @Override
    public void writeExampleFile(final String basePath, final String applicationName, final String exampleFileContent) {
        try (Writer writer = new FileWriter(GLOBAL_FILE_EXPORT_PATH + basePath + applicationName + SLASH.getValue() + applicationName + EXAMPLE_FILE_SUFFIX)) {
            writer.write(exampleFileContent);
        } catch (IOException e) {
            // do nothing
            log.error("An exception occurred: {}", e.getMessage());
        }
    }

    @Override
    public ExampleFile writeInterfaceFile(final String currentPath, final ApplicationDefinition currentApplication, final ServiceDefinition currentService, final InterfaceDefinition currentInterface, final Context context, ExampleFile exampleFile) {
        try (Writer writer = new FileWriter(currentPath + currentInterface.getPath() + FILE_TYPE_IUML)) {
            // !include file.iuml
            exampleFile.addInclude(currentService, currentInterface);
            // "call" the service
            exampleFile.addFunction(currentApplication, currentService, currentInterface);
            // Template is saved to exampleFile as it uses the same
            writer.write(templateEngine.process(exampleFile.getTemplate().getTemplateURL(), context));
        } catch (IOException io) {
            // do nothing
            log.error("An exception occurred: {}", io.getMessage());
        }
        return exampleFile;
    }

    @Override
    public void createParentDir(final String fullPathToInterfaceFile) {
        try {
            Path pathToFile = Paths.get(fullPathToInterfaceFile + FILE_TYPE_IUML);
            Files.createDirectories(pathToFile.getParent());
        } catch (IOException ioe) {
            log.error("Exception occurred:", ioe);
        }
    }

    @Override
    public String createDirectory(final String basePath, String path, Map<String, String> dirsCreate, final String applicationName) {
        if (!dirsCreate.containsKey(applicationName)) {
            path = GLOBAL_FILE_EXPORT_PATH + basePath + StringUtils.capitalize(applicationName) + SLASH.getValue();
            try {
                Files.createDirectories(Paths.get(path));
                dirsCreate.put(applicationName, path);
            } catch (IOException ioe) {
                log.error("Could not create directory {}{} for application {}", basePath, path, applicationName);
            }
        }
        return path;
    }

    @Override
    public void writeComponentFile(final DiagramType diagramType, ComponentFile componentFile) throws IOException {

        //write a pseudo common
        Files.createDirectories(Paths.get(GLOBAL_FILE_EXPORT_PATH + diagramType.getBasePath() + COMMON_PATH));
        final String fileName = (DiagramType.COMPONENT_V1_2019_6_DIAGRAM_BASE.equals(diagramType) ||
                DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE.equals(diagramType)) ?
                COMPONENT_DEFINITION_FILE :
                PARTICIPANT_DEFINITION_FILE;
        try (Writer writer = new FileWriter(GLOBAL_FILE_EXPORT_PATH + diagramType.getBasePath() + COMMON_PATH + fileName)) {
            writer.write(componentFile.getFullFileContent());
        } catch (IOException e) {
            // do nothing
            log.error("An exception occurred: {}", e.getMessage());
        }
    }

}