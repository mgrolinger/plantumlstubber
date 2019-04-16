package com.grolinger.java.controller;

import com.grolinger.java.config.Loggable;
import com.grolinger.java.config.Services;
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
import java.time.LocalDate;
import java.util.*;

@Controller
public class MultiExportController implements Loggable {

    private static final String EMPTY = "EMPTY";
    private static final String SLASH = "/";
    private static final String PATH_SEPARATOR = SLASH;
    private static final String DIR_UP = "../";
    private static final String FILETYPE_IUML = ".iuml";
    private final FileUtils fileUtils = new FileUtils();


    private final SpringTemplateEngine templateEngine;

    @Autowired
    private MultiExportController(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @SuppressWarnings("squid:S2095")
    @GetMapping("/export/components")
    public void component(Model model) throws IOException {
        // Find all yaml files
        List<Services> applicationList = new LinkedList<>(fileUtils.findAllYamlFiles());

        // Iterate over yaml files
        String basepath = "./target/Component/";
        String path = "";
        Set<String> dirsCreate = new HashSet<>();

        for (Services services : applicationList) {
            if (services == null || services.getServices() == null) {
                continue;
            }
            // Prepare example file for every Application
            StringBuilder exampleFile = new StringBuilder();
            exampleFile.append("@startuml\n");
            exampleFile.append("' generated on ").append(LocalDate.now()).append("\n");
            exampleFile.append("!define DETAILED\n'!define UML_STRICT\n!define SHOW_TODO\n\n\n");

            String applicationName = services.getApplication();
            applicationName = getReplaceUnwantedCharacters(applicationName, false);
            path = fileUtils.createDirectory(basepath, path, dirsCreate, applicationName);

            for (Map.Entry entry : services.getServices().entrySet()) {
                String serviceName = (String) entry.getKey();
                logger().warn("Servicename: {}_{}", applicationName, serviceName);
                ContextSpec.ContextBuilder contextBuilder = ContextSpec.Builder()
                        .withOrderPrio(Integer.parseInt(services.getOrderPrio()))
                        .withColorName(DomainColorMapper.getByType(services.getColor()))
                        .withIntegrationType(services.getIntegrationType())
                        .withApplicationName(services.getApplication());
                boolean isRest = isCurrentServiceARestService(services);
                if (serviceName.equalsIgnoreCase(EMPTY)) {
                    contextBuilder.withCommonPath(DIR_UP);
                    contextBuilder.withServiceName(EMPTY);
                } else {
                    serviceName = getReplaceUnwantedCharacters(serviceName, true);
                    if (!dirsCreate.contains(applicationName + serviceName)) {
                        path = fileUtils.createServiceDirectory(basepath, dirsCreate, applicationName, serviceName);
                    }
                    contextBuilder.withServiceName(getServiceName(serviceName, isRest));
                    contextBuilder.withCommonPath(getRelativeCommonPath(serviceName));
                }

                String[] interfaces = (String[]) entry.getValue();
                exampleFile.append(processInterface(Template.COMPONENT, path, contextBuilder, applicationName, serviceName, interfaces));
            }
            exampleFile.append("@enduml");
            fileUtils.writeExampleFile(basepath, applicationName, exampleFile);
            fileUtils.writeEmptyCommonFile(basepath);
        }
    }

    private String getRelativeCommonPath(String serviceName) {
        StringBuilder cp = new StringBuilder();
        if (serviceName.contains(SLASH)) {
            for (int i = 0; i <= serviceName.split(SLASH).length; i++) {
                logger().warn(">> Servicename: {}, {}", i, serviceName);
                cp.append(DIR_UP);
            }
        } else {
            cp.append(DIR_UP).append(DIR_UP);
        }
        return cp.toString();
    }

    private String getServiceName(final String serviceName, boolean isRest) {
        String extServiceName = isRest ? serviceName : StringUtils.capitalize(serviceName);

        if (serviceName.contains(SLASH)) {
            return getReplaceUnwantedCharacters(extServiceName, false);
        } else {
            return extServiceName;
        }
    }


    @GetMapping("/export/sequences")
    public void sequence(Model model) throws IOException {
        // Find all yaml files
        List<Services> applicationList = new LinkedList<>(fileUtils.findAllYamlFiles());

        // Iterate over yaml files
        String basepath = "./target/Sequence/";
        String path = "";
        Set<String> dirsCreate = new HashSet<>();

        for (Services services : applicationList) {
            if (services == null || services.getServices() == null) {
                continue;
            }

            StringBuilder exampleFile = new StringBuilder();
            exampleFile.append("@startuml\n");
            exampleFile.append("' generated on ").append(LocalDate.now()).append("\n");
            exampleFile.append("'!define TECHNICAL\n'!define SIMPLE\n'!define SHOW_SQL\n'!define SHOW_DOCUMENT_LINK\n'!define SHOW_EXCEPTIONS\n\n");
            boolean isRest = isCurrentServiceARestService(services);
            // Prepare example file for every Application

            String applicationName = services.getApplication();
            applicationName = getReplaceUnwantedCharacters(applicationName, false);
            path = fileUtils.createDirectory(basepath, path, dirsCreate, applicationName);

            ContextSpec.ContextBuilder contextBuilder = ContextSpec.Builder()
                    .withOrderPrio(Integer.parseInt(services.getOrderPrio()))
                    .withColorName(DomainColorMapper.getByType(services.getColor()))
                    .withIntegrationType(services.getIntegrationType())
                    .withApplicationName(services.getApplication());

            for (Map.Entry entry : services.getServices().entrySet()) {
                String serviceName = (String) entry.getKey();
                logger().warn("Servicename: {}_{}", applicationName, serviceName);
                if (serviceName.equalsIgnoreCase(EMPTY)) {
                    contextBuilder.withCommonPath(DIR_UP);
                    contextBuilder.withServiceName(EMPTY);
                } else {
                    logger().info("Handle {} {} service", applicationName, serviceName);
                    serviceName = getReplaceUnwantedCharacters(serviceName, true);
                    if (!dirsCreate.contains(applicationName + serviceName)) {
                        path = fileUtils.createServiceDirectory(basepath, dirsCreate, applicationName, serviceName);
                    }
                    contextBuilder.withServiceName(getServiceName(serviceName, isRest));
                    contextBuilder.withCommonPath(getRelativeCommonPath(serviceName));
                }

                String[] interfaces = (String[]) entry.getValue();
                exampleFile.append(processInterface(Template.SEQUENCE, path, contextBuilder, applicationName, serviceName, interfaces));

            }
            exampleFile.append("@enduml");
            fileUtils.writeExampleFile(basepath, applicationName, exampleFile);
            fileUtils.writeEmptyCommonFile(basepath);
        }
    }

    private boolean isCurrentServiceARestService(final Services services) {
        boolean isRestService = false;
        if (services != null && !StringUtils.isEmpty(services.getIntegrationType())) {
            isRestService = services.getIntegrationType().toUpperCase().contains("REST");
        }
        return isRestService;
    }

    private StringBuilder processInterface(final Template template, String path, ContextSpec.ContextBuilder contextBuilder, String applicationName, String serviceName, String[] interfaces) {
        StringBuilder exampleFile = new StringBuilder();
        if (interfaces == null) {
            return exampleFile;
        }
        boolean isRoot = StringUtils.isEmpty(serviceName) || EMPTY.equalsIgnoreCase(serviceName);
        // Pull context to change it later with workaround
        Context context = contextBuilder.getContext();

        for (String interfaceName : interfaces) {
            String currentPath = path;
            //first create the parent dir and next replace chars
            if (interfaceName.contains(SLASH)) {
                fileUtils.createParentDir(currentPath + interfaceName + FILETYPE_IUML);
                currentPath = path + interfaceName.split(SLASH)[0] + SLASH;

            }
            interfaceName = getReplaceUnwantedCharacters(interfaceName, false);
            //interface
            contextBuilder.withInterfaceName(interfaceName);
            //Workaround
            context.setVariable("COMPLETE_INTERFACE_PATH", StringUtils.capitalize(applicationName) + (isRoot ? "" : fileUtils.capitalizePathParts(serviceName)) + StringUtils.capitalize(interfaceName) + "Int");
            context.setVariable("API_CREATED", applicationName.toUpperCase() + "_API" + (isRoot ? "" : "_" + fileUtils.capitalizePathParts(serviceName).toUpperCase()) + "_" + interfaceName.toUpperCase() + "_CREATED");

            exampleFile.append(writeInterfaceFile(template, currentPath, context));
        }
        return exampleFile;
    }

    private StringBuilder writeInterfaceFile(final Template template, String currentPath, Context context) {
        StringBuilder exampleFile = new StringBuilder();
        String applicationName = (String) context.getVariable("applicationName");
        String serviceName = (String)context.getVariable("serviceName");
        String interfaceName = (String) context.getVariable("interfaceName");
        try (Writer writer = new FileWriter(currentPath + interfaceName + FILETYPE_IUML)) {

            String pEx = getServicePathPrefix(serviceName);
            exampleFile.append("!include ").append(pEx).append(interfaceName).append(".iuml \n");
            String serviceCall = getServiceCallName(applicationName, serviceName);
            exampleFile.append(getReplaceUnwantedCharacters(serviceCall, false)).append("_").append(getReplaceUnwantedCharacters(interfaceName, false)).append("()\n\n");
            logger().info("Write {}_{} to {}", serviceCall, interfaceName, currentPath + interfaceName + FILETYPE_IUML);
            writer.write(templateEngine.process(template.getTemplateURL(), context));
        } catch (IOException io) {
            // do nothing
            logger().error("exception: {}", io.getMessage());
        }
        return exampleFile;
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

}