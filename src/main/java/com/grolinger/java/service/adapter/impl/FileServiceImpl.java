package com.grolinger.java.service.adapter.impl;

import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.controller.templatemodel.TemplateContent;
import com.grolinger.java.service.adapter.FileService;
import com.grolinger.java.service.data.export.ComponentFile;
import com.grolinger.java.service.data.export.ExampleFile;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.grolinger.java.controller.templatemodel.Constants.*;

@Service
public class FileServiceImpl implements FileService {
    private static final String GLOBAL_FILE_EXPORT_PATH = System.getProperty("user.dir") + File.separator + "target" + File.separator;
    private static final String COMMON_PATH = "common/";
    private static final String COMMON_FILE = "common.iuml";
    private static final String COMPONENT_DEFINITION_FILE = "component_definition.iuml";
    private static final String PARTICIPANT_DEFINITION_FILE = "participant_definition.iuml";
    public static final String FILE_TYPE_IUML = ".iuml";
    private static final String EXAMPLE_FILE_SUFFIX = "_example.puml";
    private final SpringTemplateEngine templateEngine;


    @Autowired
    private FileServiceImpl(SpringTemplateEngine templateEngine) {
        logger().info("Configure base folder to read yaml files: {}", GLOBAL_FILE_EXPORT_PATH);
        this.templateEngine = templateEngine;
    }


    @Override
    public String createServiceDirectory(final String basePath, final ServiceDefinition serviceDefinition) throws IOException {
        String path;
        String applicationPart = "";
        if (null != serviceDefinition.getApplicationName()) {
            applicationPart = serviceDefinition.getApplicationName() + PATH_SEPARATOR.getValue();
        }
        String servicePart = "";
        if (null != serviceDefinition.getServicePath()) {
            servicePart = serviceDefinition.getServicePath();
        }
        path = GLOBAL_FILE_EXPORT_PATH + basePath + applicationPart + servicePart;
        Files.createDirectories(Paths.get(path));
        return path;
    }

    @Override
    public void writeDefaultCommonFile(final String basePath, final DiagramType diagramType) throws IOException {
        //write a pseudo common
        String include = TemplateContent.INCLUDE.getContent() + " ";
        if (DiagramType.COMPONENT_DIAGRAM_BASE.equals(diagramType)) {
            include += COMPONENT_DEFINITION_FILE;
        } else {
            include += PARTICIPANT_DEFINITION_FILE;
        }
        Files.createDirectories(Paths.get(GLOBAL_FILE_EXPORT_PATH + basePath + COMMON_PATH));
        try (Writer writer = new FileWriter(GLOBAL_FILE_EXPORT_PATH + basePath + COMMON_PATH + COMMON_FILE)) {
            writer.write(TemplateContent.COMMONV2_FILE.getContent() + "\n" + include);
        } catch (IOException e) {
            // do nothing
            logger().error("Exception occurred: {}", e.getMessage());
        }
    }

    @Override
    public void writeExampleFile(final String basePath, final String applicationName, final String exampleFileContent) {
        try (Writer writer = new FileWriter(GLOBAL_FILE_EXPORT_PATH + basePath + applicationName + SLASH.getValue() + applicationName + EXAMPLE_FILE_SUFFIX)) {
            writer.write(exampleFileContent);
        } catch (IOException e) {
            // do nothing
            logger().error("Exception occurred: {}", e.getMessage());
        }
    }

    @Override
    public ExampleFile writeInterfaceFile(final String currentPath, final ServiceDefinition currentService, final InterfaceDefinition currentInterface, final Context context, ExampleFile exampleFile) {

        try (Writer writer = new FileWriter(currentPath + currentInterface.getName() + FILE_TYPE_IUML)) {
            // !include file.iuml
            exampleFile.addInclude(currentService, currentInterface);
            // "call" the service
            exampleFile.addFunction(currentService, currentInterface);
            // Template is saved to exampleFile as it uses the same
            writer.write(templateEngine.process(exampleFile.getTemplate().getTemplateURL(), context));
        } catch (IOException io) {
            // do nothing
            logger().error("exception: {}", io.getMessage());
        }
        return exampleFile;
    }

    @Override
    public void createParentDir(final String fullPathToInterfaceFile) {
        try {
            Path pathToFile = Paths.get(fullPathToInterfaceFile + FILE_TYPE_IUML);
            Files.createDirectories(pathToFile.getParent());
        } catch (IOException ioe) {
            logger().error("exception:", ioe);
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
                logger().error("Could not create directory {}{} for {}", basePath, path, applicationName);
            }
        }
        return path;
    }

    @Override
    public String getRelativeCommonPath(final String applicationName, final String serviceName, final String interfaceName) {
        return getRelativeCommonPath(applicationName + Constants.SLASH + serviceName + Constants.SLASH + interfaceName);
    }

    private String getRelativeCommonPath(final String serviceName) {
        StringBuilder cp = new StringBuilder();
        if (serviceName.contains(Constants.SLASH.getValue())) {
            for (int i = 0; i <= serviceName.split(Constants.SLASH.getValue()).length; i++) {
                logger().warn(">> Servicename: {}, {}", i, serviceName);
                cp.append(DIR_UP.getValue());
            }
        } else {
            cp.append(DIR_UP.getValue())
                    .append(DIR_UP.getValue());
        }
        return cp.toString();
    }

    @Override
    public void writeComponentFile(final DiagramType diagramType, ComponentFile componentFile) throws IOException {

        //write a pseudo common
        Files.createDirectories(Paths.get(GLOBAL_FILE_EXPORT_PATH + diagramType.getBasePath() + COMMON_PATH));
        final String fileName = DiagramType.COMPONENT_DIAGRAM_BASE.equals(diagramType) ?
                COMPONENT_DEFINITION_FILE :
                PARTICIPANT_DEFINITION_FILE;
        try (Writer writer = new FileWriter(GLOBAL_FILE_EXPORT_PATH + diagramType.getBasePath() + COMMON_PATH + fileName)) {
            writer.write(componentFile.getFullFileContent());
        } catch (IOException e) {
            // do nothing
            logger().error("Exception occurred: {}", e.getMessage());
        }
    }

}