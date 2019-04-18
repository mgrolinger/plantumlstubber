package com.grolinger.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.grolinger.java.config.Loggable;
import com.grolinger.java.config.Services;
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

import static com.grolinger.java.controller.Constants.DIR_UP;
import static com.grolinger.java.controller.Constants.FILE_TYPE_IUML;
import static com.grolinger.java.controller.TemplateContent.EOL;
import static com.grolinger.java.controller.TemplateContent.INCLUDE;

@Component
class FileUtils implements Loggable {
    private static final String SLASH = "/";
    private static final String PATH_SEPARATOR = SLASH;
    private final SpringTemplateEngine templateEngine;
    private final NameService nameService;


    @Autowired
    private FileUtils(SpringTemplateEngine templateEngine, NameService nameService) {
        this.templateEngine = templateEngine;
        this.nameService = nameService;
    }


    String createServiceDirectory(String basepath, Set<String> dirsCreate, String applicationName, String serviceName) throws IOException {
        String path;
        path = basepath + applicationName + PATH_SEPARATOR + serviceName + PATH_SEPARATOR;
        Files.createDirectories(Paths.get(path));
        dirsCreate.add(applicationName + serviceName);
        return path;
    }

    void writeEmptyCommonFile(final String basepath) throws IOException {
        //write a pseudo common
        Files.createDirectories(Paths.get(basepath + "common/"));
        try (Writer writer = new FileWriter(basepath + "common/common.iuml")) {
            writer.write(TemplateContent.COMMON_FILE.getContent());
        } catch (IOException e) {
            // do nothing
            logger().error("Exception occurred: {}", e.getMessage());
        }
    }

    void writeExampleFile(String basepath, String applicationName, StringBuilder exampleFile) {
        try (Writer writer = new FileWriter(basepath + applicationName + PATH_SEPARATOR + applicationName + "_example.puml")) {
            writer.write(exampleFile.toString());
        } catch (IOException e) {
            // do nothing
            logger().error("Exception occurred: {}", e.getMessage());
        }
    }


    StringBuilder writeInterfaceFile(final Template template, String currentPath, Context context) {
        StringBuilder exampleFile = new StringBuilder();
        String applicationName = (String) context.getVariable("applicationName");
        String serviceName = (String) context.getVariable("serviceName");
        String interfaceName = (String) context.getVariable("interfaceName");
        try (Writer writer = new FileWriter(currentPath + interfaceName + FILE_TYPE_IUML.getValue())) {

            String pEx = nameService.getServicePathPrefix(serviceName);
            exampleFile.append(INCLUDE.getContent()).append(pEx).append(interfaceName).append(FILE_TYPE_IUML.getValue()).append(EOL.getContent());
            String serviceCall = nameService.getServiceCallName(applicationName, serviceName);
            exampleFile.append(nameService.getReplaceUnwantedCharacters(serviceCall, false))
                    .append("_")
                    .append(nameService.getReplaceUnwantedCharacters(interfaceName, false))
                    .append("()").append(EOL.getContent()).append(EOL.getContent());
            logger().info("Write {}_{} to {}", serviceCall, interfaceName, currentPath + interfaceName + FILE_TYPE_IUML.getValue());
            writer.write(templateEngine.process(template.getTemplateURL(), context));
        } catch (IOException io) {
            // do nothing
            logger().error("exception: {}", io.getMessage());
        }
        return exampleFile;
    }


    void createParentDir(String fullPath) {
        try {
            Path pathToFile = Paths.get(fullPath);
            Files.createDirectories(pathToFile.getParent());
        } catch (IOException ioe) {
            logger().error("exception:", ioe);
        }
    }

    String createDirectory(String basepath, String path, Set<String> dirsCreate, String applicationName) throws IOException {
        if (!dirsCreate.contains(applicationName)) {
            path = basepath + applicationName + PATH_SEPARATOR;
            Files.createDirectories(Paths.get(path));
            dirsCreate.add(applicationName);
        }
        return path;
    }

    String getRelativeCommonPath(String serviceName) {
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

    private static class YamlPredicate {
        static boolean isYamlFile(Path path) {
            return path.toAbsolutePath().toString().toLowerCase().contains(".yaml");
        }
    }

    List<Services> findAllYamlFiles() throws IOException {
        String pathRoot = "./target/classes/";
        // TODO open file chooser for root if args given

        List<Path> collect = Files.list(Paths.get(pathRoot))
                .filter(Files::isRegularFile)
                .filter(YamlPredicate::isYamlFile)
                .collect(Collectors.toList());
        List<Services> services = new LinkedList<>();
        for (Path filesToMap : collect) {
            services.addAll(mapYamls(filesToMap));
        }
        return services;
    }

    @SuppressWarnings("squid:S2629")
    List<Services> mapYamls(Path path) {
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