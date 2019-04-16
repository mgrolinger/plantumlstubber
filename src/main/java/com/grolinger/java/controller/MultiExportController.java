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
import java.time.LocalDate;
import java.util.*;

@Controller
public class MultiExportController implements Loggable {

    private static final String EMPTY = "EMPTY";
    private static final String SLASH = "/";
    private static final String PATH_SEPARATOR = SLASH;
    private static final String DIR_UP = "../";
    private static final String FILEENDING_IUML = ".iuml";
    private static final String IS_ROOT_SERVICE = "isRootService";
    private static final String SERVICE_NAME = "serviceName";
    private List<Services> applicationList = new LinkedList<>();

    private final SpringTemplateEngine templateEngine;
    private Map<String, String> aliasMapper = new HashMap<>();

    @Autowired
    private MultiExportController(SpringTemplateEngine templateEngine) {
        aliasMapper.put("esb", "atlas");
        this.templateEngine = templateEngine;
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
            exampleFile.append("' generated on ").append(LocalDate.now()).append("\n");
            exampleFile.append("!define DETAILED\n'!define UML_STRICT\n\n\n");

            Context context = new Context();
            context.setVariable("dateCreated", LocalDate.now());
            String applicationName = services.getApplication();
            applicationName = getReplaceUnwantedCharacters(applicationName, false);
            String commonPath = "commonPath";
            path = createDirectory(basepath, path, dirsCreate, context, applicationName, commonPath);
            String colorName = services.getColor();
            context.setVariable("applicationName", applicationName);
            String applicationNameShort = aliasMapper.getOrDefault(applicationName.toLowerCase(), applicationName);
            context.setVariable("applicationNameShort", applicationNameShort);
            context.setVariable("colorType", ComponentColorMapper.getByType(colorName).getStereotype());
            context.setVariable("colorName", ComponentColorMapper.getByType(colorName));
            context.setVariable("connectionColor", ConnectionColorMapper.getByType(colorName));
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
                logger().warn("Servicename: {}_{}", applicationName, serviceName);
                if (serviceName.equalsIgnoreCase(EMPTY)) {
                    currentServiceIsRootService = true;
                    context.setVariable(commonPath, DIR_UP);
                    context.setVariable(IS_ROOT_SERVICE, true);
                    context.setVariable(SERVICE_NAME, "");
                } else {
                    currentServiceIsRootService = false;
                    context.setVariable(IS_ROOT_SERVICE, false);
                    setRelativeCommonPath(context, commonPath, serviceName);
                    serviceName = getReplaceUnwantedCharacters(serviceName, true);
                    if (!dirsCreate.contains(applicationName + serviceName)) {
                        path = createServiceDirectory(basepath, dirsCreate, applicationName, serviceName);
                        setServiceName(context, serviceName, isCurrentServiceARestService(services));
                    }
                }

                String[] interfaces = (String[]) entry.getValue();
                processInterface("componentExport.html", path, exampleFile, context, currentServiceIsRootService, applicationName, serviceName, interfaces);

            }
            exampleFile.append("@enduml");
            writeExampleFile(basepath, applicationName, exampleFile);
            writeEmptyCommonFile(basepath);
        }
    }

    private String createServiceDirectory(String basepath, Set<String> dirsCreate, String applicationName, String serviceName) throws IOException {
        String path;
        path = basepath + applicationName + PATH_SEPARATOR + serviceName + PATH_SEPARATOR;
        Files.createDirectories(Paths.get(path));
        dirsCreate.add(applicationName + serviceName);
        return path;
    }

    private void setRelativeCommonPath(Context context, String commonPath, String serviceName) {
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
    }

    private void setServiceName(Context context, final String serviceName, boolean isRest) {
        String extServiceName = isRest ? serviceName : StringUtils.capitalize(serviceName);

        if (serviceName.contains(SLASH)) {
            context.setVariable(SERVICE_NAME, getReplaceUnwantedCharacters(extServiceName, false));
        } else {

            context.setVariable(SERVICE_NAME, extServiceName);
        }
    }

    private void writeEmptyCommonFile(final String basepath) throws IOException {
        //write a pseudo common
        Files.createDirectories(Paths.get(basepath + "common/"));
        try (Writer writer = new FileWriter(basepath + "common/common.iuml")) {
            writer.write("'intentionally left empty");
        } catch (IOException e) {
            // do nothing
            logger().error("Exception occurred: {}", e.getMessage());
        }
    }

    private String capitalizePathParts(final String pathToCapitalize) {
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
            exampleFile.append("' generated on ").append(LocalDate.now()).append("\n");
            exampleFile.append("'!define TECHNICAL\n'!define SIMPLE\n'!define SHOW_SQL\n'!define SHOW_DOCUMENT_LINK\n'!define SHOW_EXCEPTIONS\n\n");

            Context context = new Context();
            context.setVariable("dateCreated", LocalDate.now());
            String applicationName = services.getApplication();
            applicationName = getReplaceUnwantedCharacters(applicationName, false);
            String commonPath = "commonPath";
            path = createDirectory(basepath, path, dirsCreate, context, applicationName, commonPath);
            String colorName = services.getColor();
            context.setVariable("applicationName", applicationName);
            String applicationNameShort = aliasMapper.getOrDefault(applicationName.toLowerCase(), applicationName);
            context.setVariable("applicationNameShort", applicationNameShort);
            context.setVariable("colorType", ComponentColorMapper.getByType(colorName).getStereotype());
            context.setVariable("orderPrio", services.getOrderPrio());
            context.setVariable("colorName", ComponentColorMapper.getByType(colorName).getValue());
            context.setVariable("connectionColor", ConnectionColorMapper.getByType(colorName).getValue());
            context.setVariable("isRestService", isCurrentServiceARestService(services));

            if (services.getServices() == null) {
                break;
            }
            for (Map.Entry entry : services.getServices().entrySet()) {
                String serviceName = (String) entry.getKey();

                if (serviceName.equalsIgnoreCase(EMPTY)) {
                    currentServiceIsRootService = true;
                    context.setVariable(commonPath, DIR_UP);
                    context.setVariable(IS_ROOT_SERVICE, true);
                } else {
                    context.setVariable(IS_ROOT_SERVICE, false);
                    StringBuilder cp = new StringBuilder();
                    if (serviceName.contains(SLASH)) {
                        for (int i = 0; i <= serviceName.split(SLASH).length; i++) {
                            cp.append(DIR_UP);
                        }
                    } else {
                        cp.append(DIR_UP).append(DIR_UP);
                    }
                    logger().info("Handle {} {} service", applicationName, serviceName);
                    context.setVariable(commonPath, cp.toString());
                    serviceName = getReplaceUnwantedCharacters(serviceName, true);
                    if (!dirsCreate.contains(applicationName + serviceName)) {
                        path = createServiceDirectory(basepath, dirsCreate, applicationName, serviceName);
                        setServiceName(context, serviceName, isCurrentServiceARestService(services));
                    }
                }

                String[] interfaces = (String[]) entry.getValue();
                processInterface("sequenceExport.html", path, exampleFile, context, currentServiceIsRootService, applicationName, serviceName, interfaces);

            }
            exampleFile.append("@enduml");
            writeExampleFile(basepath, applicationName, exampleFile);
            writeEmptyCommonFile(basepath);
        }
    }

    private boolean isCurrentServiceARestService(final Services services) {
        boolean isRestService = false;
        if (services != null && !StringUtils.isEmpty(services.getIntegrationType())) {
            isRestService = services.getIntegrationType().toUpperCase().contains("REST");
        }
        return isRestService;
    }

    private void writeExampleFile(String basepath, String applicationName, StringBuilder exampleFile) {
        try (Writer writer = new FileWriter(basepath + applicationName + PATH_SEPARATOR + applicationName + "_example.puml")) {
            writer.write(exampleFile.toString());
        } catch (IOException e) {
            // do nothing
            logger().error("Exception occurred: {}", e.getMessage());
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
        String pathRoot = "./target/classes/";
        // TODO open file chooser for root if args given
        Files.list(Paths.get(pathRoot))
                .filter(Files::isRegularFile)
                .filter(YamlPredicate::isYamlFile)
                .forEach(this::mapYamls);
    }


    private void processInterface(final String template, String path, StringBuilder exampleFile, Context context, boolean isRoot, String applicationName, String serviceName, String[] interfaces) {
        if (interfaces == null) {
            return;
        }
        for (String interfaceName : interfaces) {
            String currentPath = path;
            //first create the parent dir and next replace chars
            if (interfaceName.contains(SLASH)) {
                createParentDir(currentPath + interfaceName + FILEENDING_IUML);
                currentPath = path + interfaceName.split(SLASH)[0] + SLASH;

            }
            interfaceName = getReplaceUnwantedCharacters(interfaceName, false);
            //interface
            context.setVariable("interfaceName", interfaceName);
            context.setVariable("COMPLETE_INTERFACE_PATH", StringUtils.capitalize(applicationName) + (isRoot ? "" : capitalizePathParts(serviceName)) + StringUtils.capitalize(interfaceName) + "Int");
            context.setVariable("API_CREATED", applicationName.toUpperCase() + "_API" + (isRoot ? "" : "_" + capitalizePathParts(serviceName).toUpperCase()) + "_" + interfaceName.toUpperCase() + "_CREATED");

            try (Writer writer = new FileWriter(currentPath + interfaceName + FILEENDING_IUML)) {
                String pEx = getServicePathPrefix(serviceName);
                exampleFile.append("!include ").append(pEx).append(interfaceName).append(".iuml \n");
                String serviceCall = getServiceCallName(applicationName, serviceName);
                exampleFile.append(getReplaceUnwantedCharacters(serviceCall, false)).append("_").append(getReplaceUnwantedCharacters(interfaceName, false)).append("()\n\n");
                logger().info("Write {}_{} to {}", serviceCall, interfaceName, currentPath + interfaceName + FILEENDING_IUML);
                writer.write(templateEngine.process(template, context));
            } catch (IOException io) {
                // do nothing
                logger().error("exception: {}", io.getMessage());
            }
        }
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
            logger().error("mapYamls exception: {}", e.getMessage());
        }
    }
}