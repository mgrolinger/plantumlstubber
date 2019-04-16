package com.grolinger.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.grolinger.java.config.Loggable;
import com.grolinger.java.config.Services;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.StringUtils;

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

class FileUtils  implements Loggable {
    private static final String EMPTY = "EMPTY";
    private static final String SLASH = "/";
    private static final String PATH_SEPARATOR = SLASH;
    private static final String DIR_UP = "../";
    private static final String FILEENDING_IUML = ".iuml";

    String capitalizePathParts(final String pathToCapitalize) {
        return capitalizeStringParts(capitalizeStringParts(pathToCapitalize, SLASH), "_");
    }

    private String capitalizeStringParts(final String pathToCapitalize, final String splitChar) {
        StringBuilder result = new StringBuilder();
        if (pathToCapitalize != null) {
            if (pathToCapitalize.contains(splitChar)) {
                String[] parts = pathToCapitalize.split(splitChar);
                for (String part : parts) {
                    result.append(StringUtils.capitalize(part.replace(splitChar, "")));
                }
            } else {
                result.append(StringUtils.capitalize(pathToCapitalize));
            }
        }
        return result.toString();
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
            writer.write("'intentionally left empty");
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