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
import org.springframework.util.ResourceUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
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
    private static final String GLOBAL_FILE_EXPORT_PATH = System.getProperty("user.dir") + File.separator + "target" + File.separator;
    private static final String COMMON_PATH = "common/";
    private static final String COMMON_FILE = "common/common.iuml";
    private static final String COMPONENT_DEFINITION_FILE = "component_definition.iuml";
    private static final String PARTICIPANT_DEFINITION_FILE = "participant_definition.iuml";
    private static final String EXAMPLE_FILE_SUFFIX = "_example.puml";
    private static final String EXCEPTION_OCCURRED = "An exception occurred: {}";

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
        if (DiagramType.COMPONENT_V1_2019_6_DIAGRAM_BASE.equals(diagramType) ||
                DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE.equals(diagramType)) {
            filesToExport = Arrays.asList(
                    COMMON_FILE,
                    COMMON_PATH + "domain_legend.iuml",
                    COMMON_PATH + "devicons/springBoot.puml",
                    COMMON_PATH + "devicons/salesforce.puml",
                    COMMON_PATH + "devicons/sap.puml",
                    COMMON_PATH + "devicons/solr.puml",
                    COMMON_PATH + "devicons/tibco.puml",
                    "Readme.puml");
            // FIXME: better solution than hardcoding here
            Files.createDirectories(Paths.get(GLOBAL_FILE_EXPORT_PATH + basePath + COMMON_PATH));
            Files.createDirectories(Paths.get(GLOBAL_FILE_EXPORT_PATH + basePath + COMMON_PATH + "devicons/"));
        } else {
            filesToExport = Arrays.asList(
                    COMMON_FILE,
                    COMMON_PATH + "bipro_messageIds.iuml",
                    COMMON_PATH + "http_statuscodes.iuml");
            Files.createDirectories(Paths.get(GLOBAL_FILE_EXPORT_PATH + basePath + COMMON_PATH));
        }

        for (String currentFileName : filesToExport) {
            File currentFile = ResourceUtils.getFile("classpath:static_includes/" + diagramType.getBasePath() + currentFileName);

            //Read File Content
            String content = new String(Files.readAllBytes(currentFile.toPath()));
            try (Writer writer = new FileWriter(GLOBAL_FILE_EXPORT_PATH + basePath + currentFileName)) {
                writer.write(content);
            } catch (IOException e) {
                // do nothing
                log.error(EXCEPTION_OCCURRED, e.getMessage());
            }
        }
    }

    @Override
    public void writeDefaultSkinFiles() throws IOException {
        List<String> filesToExport = Arrays.asList(
                "skin/ci_company_colors.iuml",
                "skin/default.skin",
                "skin/darcula.skin");
        log.info("Write some skin files: {}", filesToExport);
        Files.createDirectories(Paths.get(GLOBAL_FILE_EXPORT_PATH + "skin/"));
        //skin
        for (String currentFileName : filesToExport) {
            File currentFile = ResourceUtils.getFile("classpath:static_includes/" + currentFileName);

            //Read File Content
            String content = new String(Files.readAllBytes(currentFile.toPath()));
            try (Writer writer = new FileWriter(GLOBAL_FILE_EXPORT_PATH + currentFileName)) {
                writer.write(content);
            } catch (IOException e) {
                // do nothing
                log.error(EXCEPTION_OCCURRED, e.getMessage());
            }
        }
    }

    @Override
    public void writeExampleFile(final String basePath, final String applicationName, final String exampleFileContent) {
        try (Writer writer = new FileWriter(GLOBAL_FILE_EXPORT_PATH + basePath + applicationName + SLASH.getValue() + applicationName + EXAMPLE_FILE_SUFFIX)) {
            writer.write(exampleFileContent);
        } catch (IOException e) {
            // do nothing
            log.error(EXCEPTION_OCCURRED, e.getMessage());
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
            log.error(EXCEPTION_OCCURRED, io.getMessage());
        }
        return exampleFile;
    }

    @Override
    public void createParentDir(final String fullPathToInterfaceFile) {
        try {
            Path pathToFile = Paths.get(fullPathToInterfaceFile + FILE_TYPE_IUML);
            if (pathToFile != null) {
                Files.createDirectories(pathToFile.getParent());
            }
        } catch (IOException ioe) {
            log.error("Exception occurred:", ioe);
        }
    }

    @Override
    public String createDirectory(final String basePath, String path, Map<String, String> dirsCreate, final String applicationName) {
        if (!dirsCreate.containsKey(applicationName)) {
            path = GLOBAL_FILE_EXPORT_PATH + basePath + StringUtils.capitalize(applicationName) + PATH_SEPARATOR.getValue();
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
            log.error(EXCEPTION_OCCURRED, e.getMessage());
        }
    }

}