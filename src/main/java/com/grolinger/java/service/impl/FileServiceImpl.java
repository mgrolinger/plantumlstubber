package com.grolinger.java.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.grolinger.java.config.Services;
import com.grolinger.java.controller.templateModel.Constants;
import com.grolinger.java.controller.templateModel.Template;
import com.grolinger.java.controller.templateModel.TemplateContent;
import com.grolinger.java.service.NameService;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.grolinger.java.controller.templateModel.Constants.DIR_UP;
import static com.grolinger.java.controller.templateModel.Constants.FILE_TYPE_IUML;
import static com.grolinger.java.controller.templateModel.ContextVariables.*;
import static com.grolinger.java.controller.templateModel.TemplateContent.EOL;
import static com.grolinger.java.controller.templateModel.TemplateContent.INCLUDE;

@Component
public class FileServiceImpl implements com.grolinger.java.service.FileService {
    private static final String SLASH = "/";
    private static final String PATH_SEPARATOR = SLASH;
    private static final String COMMON_PATH = "common/";
    private static final String COMMON_FILE = "common.iuml";
    private static final String EXAMPLE_FILE_SUFFIX = "_example.puml";
    private final SpringTemplateEngine templateEngine;
    private final NameService nameService;


    @Autowired
    private FileServiceImpl(SpringTemplateEngine templateEngine, NameService nameService) {
        this.templateEngine = templateEngine;
        this.nameService = nameService;
    }


    @Override
    public String createServiceDirectory(final String basePath, Set<String> dirsCreate, final String applicationName, final String serviceName) throws IOException {
        String path;
        path = basePath + applicationName + PATH_SEPARATOR + serviceName + PATH_SEPARATOR;
        Files.createDirectories(Paths.get(path));
        dirsCreate.add(applicationName + serviceName);
        return path;
    }

    @Override
    public void writeEmptyCommonFile(final String basePath) throws IOException {
        //write a pseudo common
        Files.createDirectories(Paths.get(basePath + COMMON_PATH));
        try (Writer writer = new FileWriter(basePath + COMMON_PATH + COMMON_FILE)) {
            writer.write(TemplateContent.COMMON_FILE.getContent());
        } catch (IOException e) {
            // do nothing
            logger().error("Exception occurred: {}", e.getMessage());
        }
    }

    @Override
    public void writeExampleFile(final String basePath, final String applicationName, final StringBuilder exampleFile) {
        try (Writer writer = new FileWriter(basePath + applicationName + PATH_SEPARATOR + applicationName + EXAMPLE_FILE_SUFFIX)) {
            writer.write(exampleFile.toString());
        } catch (IOException e) {
            // do nothing
            logger().error("Exception occurred: {}", e.getMessage());
        }
    }

    @Override
    public StringBuilder writeInterfaceFile(final Template template, final String currentPath, final Context context) {
        StringBuilder exampleFile = new StringBuilder();
        String applicationName = (String) context.getVariable(APPLICATION_NAME.getName());
        String serviceName = (String) context.getVariable(SERVICE_NAME.getName());
        String interfaceName = (String) context.getVariable(INTERFACE_NAME.getName());
        try (Writer writer = new FileWriter(currentPath + interfaceName + FILE_TYPE_IUML.getValue())) {

            String pEx = nameService.getServiceNameSuffix(serviceName);
            exampleFile.append(INCLUDE.getContent()).append(pEx).append(interfaceName).append(FILE_TYPE_IUML.getValue()).append(EOL.getContent());
            String serviceCall = nameService.getServiceCallName(applicationName, serviceName);
            exampleFile.append(nameService.replaceUnwantedCharacters(serviceCall, false))
                    .append(Constants.NAME_SEPARATOR)
                    .append(nameService.replaceUnwantedCharacters(interfaceName, false))
                    .append("()")
                    .append(EOL.getContent()).append(EOL.getContent());
            logger().info("Write {}_{} to {}{}{}", serviceCall, interfaceName, currentPath, interfaceName, FILE_TYPE_IUML.getValue());
            writer.write(templateEngine.process(template.getTemplateURL(), context));
        } catch (IOException io) {
            // do nothing
            logger().error("exception: {}", io.getMessage());
        }
        return exampleFile;
    }

    @Override
    public void createParentDir(final String fullPath) {
        try {
            Path pathToFile = Paths.get(fullPath);
            Files.createDirectories(pathToFile.getParent());
        } catch (IOException ioe) {
            logger().error("exception:", ioe);
        }
    }

    @Override
    public String createDirectory(final String basePath, String path, Set<String> dirsCreate, final String applicationName) {
        if (!dirsCreate.contains(applicationName)) {
            path = basePath + applicationName + PATH_SEPARATOR;
            try {
                Files.createDirectories(Paths.get(path));
                dirsCreate.add(applicationName);
            } catch (IOException ioe) {
                logger().error("Could not create directory {}{} for {}", basePath, path, applicationName);
            }
        }
        return path;
    }

    @Override
    public String getRelativeCommonPath(final String serviceName) {
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
    public List<Services> findAllYamlFiles() {
        String pathRoot = "./target/classes/";
        List<Path> collect = new LinkedList<>();
        // TODO open file chooser for root if args given
        try {
            collect = Files.list(Paths.get(pathRoot))
                    .filter(Files::isRegularFile)
                    .filter(YamlPredicate::isYamlFile)
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            logger().error("IOException {}", ioe.getMessage());
        }
        List<Services> services = new LinkedList<>();
        for (Path filesToMap : collect) {
            services.addAll(mapYamls(filesToMap));
        }
        return services;
    }


    @Override
    public List<Services> mapYamls(final Path path) {
        List<Services> servicesList = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Services services = mapper.readValue(path.toFile(), Services.class);
            logger().info(ReflectionToStringBuilder.toString(services, ToStringStyle.MULTI_LINE_STYLE));
            servicesList.add(services);
        } catch (Exception e) {
            //Do nothing
            logger().error("mapYamls exception: {}", e.getMessage());
        }
        return servicesList;
    }
}