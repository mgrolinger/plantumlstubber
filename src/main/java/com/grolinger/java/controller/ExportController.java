package com.grolinger.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.grolinger.java.config.Loggable;
import com.grolinger.java.config.Services;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class ExportController implements Loggable {

    private static final String EMPTY = "EMPTY";
    private static final String SLASH = "/";
    private static final String PATH_SEPARATOR = SLASH;
    private static final String DIR_UP = "../";
    private List<Services> applicationList = new LinkedList<>();

    @Autowired
    private SpringTemplateEngine templateEngine;
    private Map<String, String> aliasMapper = new HashMap<>();

    private ExportController() {

        aliasMapper.put("esb", "atlas");
    }

    @SuppressWarnings("squid:S2095")
    @GetMapping("/export/components")
    public void component(Model model) throws IOException {

        // Find all yaml files
        findAllYamlFiles();

        // Iterate over yaml files
        String basepath = "./target/Component/";
        String path = "";
        Set<String> dirsCreate = new HashSet<>();

        for (Services services : applicationList) {
            boolean currentServiceIsRootService = false;
            // Prepare example file for every Application
            StringBuilder exampleFile = new StringBuilder();
            exampleFile.append("@startuml\n");
            exampleFile.append("' generated on ").append(LocalDateTime.now()).append("\n");
            exampleFile.append("!define DETAILED\n'!define UML_STRICT\n\n\n");

            Context context = new Context();
            context.setVariable("dateCreated", LocalDateTime.now());
            String applicationName = services.getApplication();
            applicationName = getReplaceUnwantedCharacters(applicationName, false);
            String commonPath = "commonPath";
            path = createDirectory(basepath, path, dirsCreate, context, applicationName, commonPath);
            String colorName = services.getColor();
            context.setVariable("applicationName", applicationName);
            String applicationNameShort = aliasMapper.getOrDefault(applicationName.toLowerCase(), applicationName);
            context.setVariable("applicationNameShort", applicationNameShort);
            context.setVariable("colorType", "<<" + colorName.toLowerCase() + ">>");
            context.setVariable("colorName", ComponentColorMapper.getByType(colorName.toLowerCase()));
            context.setVariable("connectionColor", ConnectionColorMapper.getByType(colorName.toLowerCase()));
            String integrationType = "";
            if (!StringUtils.isEmpty(services.getIntegrationType())) {
                integrationType = "INTEGRATION_TYPE(" + services.getIntegrationType() + ")";
            }
            context.setVariable("integrationType", integrationType);

            if (services.getServices() == null) {
                break;
            }
            for (Map.Entry entry : services.getServices().entrySet()) {
                String serviceName = (String) entry.getKey();
                logger().warn("Servicename: {}", serviceName);
                if (serviceName.equalsIgnoreCase(EMPTY)) {
                    currentServiceIsRootService = true;
                    context.setVariable(commonPath, DIR_UP);
                    context.setVariable("isRootService", true);
                } else {
                    currentServiceIsRootService = false;
                    context.setVariable("isRootService", false);
                    StringBuilder cp = new StringBuilder();
                    if (serviceName.contains(SLASH)) {
                        for (int i = 0; i <= serviceName.split(SLASH).length; i++) {
                            logger().warn(">> Servicename: {}, {}", i, serviceName);
                            cp.append(DIR_UP);
                        }
                    } else {
                        cp.append(DIR_UP).append(DIR_UP);
                    }
                    context.setVariable(commonPath, cp.toString());
                    serviceName = getReplaceUnwantedCharacters(serviceName, true);
                    if (!dirsCreate.contains(applicationName + serviceName)) {
                        path = basepath + applicationName + PATH_SEPARATOR + serviceName + PATH_SEPARATOR;
                        Files.createDirectories(Paths.get(path));
                        dirsCreate.add(applicationName + serviceName);
                        if (serviceName.contains(SLASH)) {
                            // Get rid of toms API entry point in the service name
                            context.setVariable("serviceName", getReplaceUnwantedCharacters(serviceName, false));
                        } else {
                            context.setVariable("serviceName", serviceName);
                        }
                    }
                }

                String[] interfaces = (String[]) entry.getValue();
                if (!processInterface("componentExport.html", path, exampleFile, context, currentServiceIsRootService, applicationName, serviceName, interfaces))
                    break;

            }
            exampleFile.append("@enduml");
            try (Writer writer = new FileWriter(basepath + applicationName + PATH_SEPARATOR + applicationName + "_example.puml")) {
                writer.write(exampleFile.toString());
            } catch (IOException e) {
                // do nothing
                logger().error("Exception occurred: {}", e);
            }
            //write a pseudo common
            Files.createDirectories(Paths.get(basepath+"common/"));
            try (Writer writer = new FileWriter(basepath+"common/common.puml")) {
                writer.write("'intentionally left empty");
            } catch (IOException e) {
                // do nothing
                logger().error("Exception occurred: {}", e);
            }
        }
    }

    public String capitalizePathParts(final String pathToCapitalize) {
        StringBuilder result = new StringBuilder();
        return result.append(capitalizeStringParts(capitalizeStringParts(pathToCapitalize,SLASH),"_")).toString();
    }
    public String capitalizeStringParts(final String pathToCapitalize, final String splitChar){
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

    @GetMapping("/export/sequences")
    public void sequence(Model model) throws IOException {

        // Find all yaml files
        findAllYamlFiles();

        // Iterate over yaml files
        String basepath = "./target/Sequence/";
        String path = "";
        Set<String> dirsCreate = new HashSet<>();

        for (Services services : applicationList) {
            boolean currentServiceIsRootService = false;
            // Prepare example file for every Application
            StringBuilder exampleFile = new StringBuilder();
            exampleFile.append("@startuml\n");
            exampleFile.append("' generated on ").append(LocalDateTime.now()).append("\n");
            exampleFile.append("!define TECHNICAL\n'!define SHOW_SQL\n'!define SHOW_DOCUMENT_LINK\n'!define SHOW_EXCEPTIONS\n\n");

            Context context = new Context();
            context.setVariable("dateCreated", LocalDateTime.now());
            String applicationName = services.getApplication();
            applicationName = getReplaceUnwantedCharacters(applicationName, false);
            String commonPath = "commonPath";
            path = createDirectory(basepath, path, dirsCreate, context, applicationName, commonPath);
            String colorName = services.getColor();
            context.setVariable("applicationName", applicationName);
            String applicationNameShort = aliasMapper.getOrDefault(applicationName.toLowerCase(), applicationName);
            context.setVariable("applicationNameShort", applicationNameShort);
            context.setVariable("colorType", "<<" + colorName.toLowerCase() + ">>");
            context.setVariable("orderPrio",services.getOrderPrio());
            context.setVariable("colorName", ComponentColorMapper.getByType(colorName.toLowerCase()).getValue());
            context.setVariable("connectionColor", ConnectionColorMapper.getByType(colorName.toLowerCase()).getValue());
            String integrationType = "";
            context.setVariable("isRestService", false);
            if (!StringUtils.isEmpty(services.getIntegrationType())) {
                integrationType = services.getIntegrationType() + "_INTEGRATION_TYPE";
                context.setVariable("isRestService", "rest".equalsIgnoreCase(services.getIntegrationType()));
            }
            context.setVariable("integrationType", integrationType);

            if (services.getServices() == null) {
                break;
            }
            for (Map.Entry entry : services.getServices().entrySet()) {
                String serviceName = (String) entry.getKey();

                if (serviceName.equalsIgnoreCase(EMPTY)) {
                    currentServiceIsRootService = true;
                    context.setVariable(commonPath, DIR_UP);
                    context.setVariable("isRootService", true);
                } else {
                    context.setVariable("isRootService", false);
                    StringBuilder cp = new StringBuilder();
                    if (serviceName.contains(SLASH)) {
                        for (int i = 0; i <= serviceName.split(SLASH).length; i++) {
                            cp.append(DIR_UP);
                        }
                    } else {
                        cp.append(DIR_UP).append(DIR_UP);
                    }
                    logger().info("Handle {} {} service", applicationName, integrationType);
                    context.setVariable(commonPath, cp.toString());
                    serviceName = getReplaceUnwantedCharacters(serviceName, true);
                    if (!dirsCreate.contains(applicationName + serviceName)) {
                        path = basepath + applicationName + PATH_SEPARATOR + serviceName + PATH_SEPARATOR;
                        Files.createDirectories(Paths.get(path));
                        dirsCreate.add(applicationName + serviceName);
                        if (serviceName.contains(SLASH)) {
                            context.setVariable("serviceName", getReplaceUnwantedCharacters(serviceName, false));
                        } else {
                            context.setVariable("serviceName", serviceName);
                        }
                    }
                }

                String[] interfaces = (String[]) entry.getValue();
                if (!processInterface("sequenceExport.html", path, exampleFile, context, currentServiceIsRootService, applicationName, serviceName, interfaces))
                    break;

            }
            exampleFile.append("@enduml");
            try (Writer writer = new FileWriter(basepath + applicationName + PATH_SEPARATOR + applicationName + "_example.puml")) {
                writer.write(exampleFile.toString());
            } catch (IOException e) {
                // do nothing
                logger().error("Exception occurred: {}", e);
            }
        }
    }

    private String createDirectory(String basepath, String path, Set<String> dirsCreate, Context context, String applicationName, String commonPath) throws IOException {
        if (!dirsCreate.contains(applicationName)) {
            context.setVariable(commonPath, DIR_UP);
            path = basepath + applicationName + PATH_SEPARATOR;
            Files.createDirectories(Paths.get(path));
            dirsCreate.add(applicationName);
        }
        return path;
    }

    private void findAllYamlFiles() throws IOException {
        Files.list(Paths.get("./target/classes/"))
                .filter(Files::isRegularFile)
                .filter(YamlPredicate::isYamlFile)
                .forEach(this::mapYamls);
    }


    private boolean processInterface(final String template, String path, StringBuilder exampleFile, Context context, boolean isRoot, String applicationName, String serviceName, String[] interfaces) {
        if (interfaces == null) {
            return false;
        }
        for (String interfaceName : interfaces) {
            String currentPath = path;
            //first create the parent dir and next replace chars
            if (interfaceName.contains(SLASH)) {
                createParentDir(currentPath + interfaceName + ".iuml");
                currentPath = path + interfaceName.split(SLASH)[0] + SLASH;

            }
            interfaceName = getReplaceUnwantedCharacters(interfaceName, false);
            //interface
            context.setVariable("interfaceName", interfaceName);
            context.setVariable("COMPLETE_INTERFACE_PATH", StringUtils.capitalize(applicationName) + (isRoot ? "" : capitalizePathParts(serviceName)) + StringUtils.capitalize(interfaceName) + "Int");
            context.setVariable("API_CREATED", applicationName.toUpperCase() + "_API" + (isRoot ? "" : "_" + capitalizePathParts(serviceName).toUpperCase()) + "_" + interfaceName.toUpperCase() + "_CREATED");

            try (Writer writer = new FileWriter(currentPath + interfaceName + ".iuml")) {
                String pEx = getServicePathPrefix(serviceName);
                exampleFile.append("!include ").append(pEx).append(interfaceName).append(".iuml \n");
                String serviceCall = getServiceCallName(applicationName, serviceName);
                exampleFile.append(getReplaceUnwantedCharacters(serviceCall,false)).append("_").append(getReplaceUnwantedCharacters(interfaceName,false)).append("()\n\n");
                logger().info("Write {}_{} to {}", serviceCall, interfaceName, currentPath + interfaceName + ".iuml");
                writer.write(templateEngine.process(template, context));
            } catch (IOException io) {
                // do nothing
                logger().error("exception: {}", io);
            }
        }
        return true;
    }

    private void createParentDir(String fullPath) {
        try {
            Path pathToFile = Paths.get(fullPath);
            Files.createDirectories(pathToFile.getParent());
        } catch (IOException ioe) {
            logger().error("exception:", ioe);
        }
    }

    private String getServiceCallName(String applicationName, String serviceName) {
        return (serviceName.equalsIgnoreCase(EMPTY)) ? applicationName : applicationName + "_" + serviceName;
    }

    private String getServicePathPrefix(String serviceName) {
        return (serviceName.equalsIgnoreCase(EMPTY)) ? "" : serviceName + PATH_SEPARATOR;
    }

    private String getReplaceUnwantedCharacters(String name, boolean replaceDotsOnly) {
        String newName = name.replace('.', '_');
        if (!replaceDotsOnly)
            newName = newName.replace('/', '_');
        return newName;
    }

    private static class YamlPredicate {
        static boolean isYamlFile(Path path) {
            return path.toAbsolutePath().toString().toLowerCase().contains(".yaml");
        }
    }

    @SuppressWarnings("squid:S2629")
    private void mapYamls(Path path) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Services services = mapper.readValue(path.toFile(), Services.class);
            logger().info(ReflectionToStringBuilder.toString(services, ToStringStyle.MULTI_LINE_STYLE));
            applicationList.add(services);
        } catch (Exception e) {
            //Do nothing
            logger().error("mapYamls exception: ", e);
        }
    }
}