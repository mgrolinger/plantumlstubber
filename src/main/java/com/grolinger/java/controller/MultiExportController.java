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
    private static final String FILEENDING_IUML = ".iuml";
    private static final String IS_ROOT_SERVICE = "isRootService";
    private static final String SERVICE_NAME = "serviceName";
    private final FileUtils fileUtils = new FileUtils();


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
        List<Services> applicationList = new LinkedList<>(fileUtils.findAllYamlFiles());

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
            exampleFile.append("!define DETAILED\n'!define UML_STRICT\n!define SHOW_TODO\n\n\n");
            Context context = new Context();
            context.setVariable("dateCreated", LocalDate.now());
            String applicationName = services.getApplication();
            applicationName = getReplaceUnwantedCharacters(applicationName, false);
            String commonPath = "commonPath";
            path = fileUtils.createDirectory(basepath, path, dirsCreate, context, applicationName, commonPath);
            String colorName = services.getColor();
            context.setVariable("applicationName", applicationName);
            String applicationNameShort = aliasMapper.getOrDefault(applicationName.toLowerCase(), applicationName);
            context.setVariable("applicationNameShort", applicationNameShort);
            context.setVariable("colorType", DomainColorMapper.getByType(colorName).getStereotype());
            context.setVariable("colorName", DomainColorMapper.getByType(colorName));
            context.setVariable("connectionColor", ConnectionColorMapper.getByType(colorName));
            String integrationType = "";
            if (!StringUtils.isEmpty(services.getIntegrationType())) {
                integrationType = "INTEGRATION_TYPE(" + services.getIntegrationType() + ")";
            }
            context.setVariable("componentIntegrationType", integrationType);

            if (services.getServices() == null) {
                break;
            }
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
                    currentServiceIsRootService = true;
                    context.setVariable(commonPath, DIR_UP);
                    context.setVariable(IS_ROOT_SERVICE, currentServiceIsRootService);
                    context.setVariable(SERVICE_NAME, "");
                    contextBuilder.withCommonPath(DIR_UP);
                    contextBuilder.withServiceName(EMPTY);
                } else {
                    currentServiceIsRootService = false;
                    context.setVariable(IS_ROOT_SERVICE, currentServiceIsRootService);
                    setRelativeCommonPath(context, commonPath, serviceName);
                    serviceName = getReplaceUnwantedCharacters(serviceName, true);
                    if (!dirsCreate.contains(applicationName + serviceName)) {
                        path = fileUtils.createServiceDirectory(basepath, dirsCreate, applicationName, serviceName);
                        setServiceName(context, serviceName, isRest);
                    }
                    contextBuilder.withServiceName(getServiceName(serviceName,isRest));
                    contextBuilder.withCommonPath(getRelativeCommonPath(serviceName));
                }

                String[] interfaces = (String[]) entry.getValue();
                //processInterface("componentExport.html", path, exampleFile, context, currentServiceIsRootService, applicationName, serviceName, interfaces);
                processInterface("componentExport.html", path, exampleFile, contextBuilder, currentServiceIsRootService, applicationName, serviceName, interfaces);
            }
            exampleFile.append("@enduml");
            fileUtils.writeExampleFile(basepath, applicationName, exampleFile);
            fileUtils.writeEmptyCommonFile(basepath);
        }
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

    private void setServiceName(Context context, final String serviceName, boolean isRest) {
        String extServiceName = isRest ? serviceName : StringUtils.capitalize(serviceName);

        if (serviceName.contains(SLASH)) {
            context.setVariable(SERVICE_NAME, getReplaceUnwantedCharacters(extServiceName, false));
        } else {

            context.setVariable(SERVICE_NAME, extServiceName);
        }
    }

    private String getServiceName(final String serviceName, boolean isRest) {
        String extServiceName = isRest ? serviceName : StringUtils.capitalize(serviceName);

        if (serviceName.contains(SLASH)) {
            return getReplaceUnwantedCharacters(extServiceName, false);
        } else {

            return extServiceName;
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
        List<Services> applicationList = new LinkedList<>(fileUtils.findAllYamlFiles());

        // Iterate over yaml files
        String basepath = "./target/Sequence/";
        String path = "";
        Set<String> dirsCreate = new HashSet<>();

        for (Services services : applicationList) {
            boolean currentServiceIsRootService = false;
            boolean isRest = isCurrentServiceARestService(services);
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
            path = fileUtils.createDirectory(basepath, path, dirsCreate, context, applicationName, commonPath);
            String colorName = services.getColor();
            context.setVariable("applicationName", applicationName);
            String applicationNameShort = aliasMapper.getOrDefault(applicationName.toLowerCase(), applicationName);
            context.setVariable("applicationNameShort", applicationNameShort);
            context.setVariable("colorType", DomainColorMapper.getByType(colorName).getStereotype());
            context.setVariable("orderPrio", services.getOrderPrio());
            context.setVariable("colorName", DomainColorMapper.getByType(colorName).getValue());
            context.setVariable("connectionColor", ConnectionColorMapper.getByType(colorName).getValue());
            ContextSpec.ContextBuilder contextBuilder = ContextSpec.Builder()
                    .withOrderPrio(Integer.parseInt(services.getOrderPrio()))
                    .withColorName(DomainColorMapper.getByType(services.getColor()))
                    .withIntegrationType(services.getIntegrationType())
                    .withApplicationName(services.getApplication());


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
                    contextBuilder.withCommonPath(DIR_UP);
                    contextBuilder.withServiceName(EMPTY);
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
                        path = fileUtils.createServiceDirectory(basepath, dirsCreate, applicationName, serviceName);
                        setServiceName(context, serviceName, isCurrentServiceARestService(services));
                    }
                    contextBuilder.withServiceName(getServiceName(serviceName,isRest));
                    contextBuilder.withCommonPath(getRelativeCommonPath(serviceName));
                }

                String[] interfaces = (String[]) entry.getValue();
                processInterface("sequenceExport.html", path, exampleFile, contextBuilder, currentServiceIsRootService, applicationName, serviceName, interfaces);

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

    private void processInterface(final String template, String path, StringBuilder exampleFile, Context context, boolean isRoot, String applicationName, String serviceName, String[] interfaces) {
        if (interfaces == null) {
            return;
        }
        for (String interfaceName : interfaces) {
            String currentPath = path;
            //first create the parent dir and next replace chars
            if (interfaceName.contains(SLASH)) {
                fileUtils.createParentDir(currentPath + interfaceName + FILEENDING_IUML);
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

    private void processInterface(final String template, String path, StringBuilder exampleFile, ContextSpec.ContextBuilder contextBuilder, boolean isRoot, String applicationName, String serviceName, String[] interfaces) {
        if (interfaces == null) {
            return;
        }
        for (String interfaceName : interfaces) {
            String currentPath = path;
            //first create the parent dir and next replace chars
            if (interfaceName.contains(SLASH)) {
                fileUtils.createParentDir(currentPath + interfaceName + FILEENDING_IUML);
                currentPath = path + interfaceName.split(SLASH)[0] + SLASH;

            }
            interfaceName = getReplaceUnwantedCharacters(interfaceName, false);
            //interface
            contextBuilder.withInterfaceName(interfaceName);
            Context context = contextBuilder.getContext();
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