package com.grolinger.java.controller;

import com.grolinger.java.config.Loggable;
import com.grolinger.java.config.Services;
import com.grolinger.java.service.DecisionService;
import com.grolinger.java.service.NameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static com.grolinger.java.controller.Constants.*;
import static com.grolinger.java.controller.TemplateContent.*;

@Controller
public class MultiExportController implements Loggable {
    private final FileUtils fileUtils;
    private NameService nameService;
    private DecisionService decisionService;


    @Autowired
    private MultiExportController(FileUtils fileUtils, NameService nameService, DecisionService decisionService) {
        this.fileUtils = fileUtils;
        this.nameService = nameService;
        this.decisionService = decisionService;

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
            exampleFile.append(START.getContent());
            exampleFile.append(DATE.getContent()).append(LocalDate.now()).append(EOL.getContent());
            exampleFile.append(COMPONENT_HEADER.getContent());

            String applicationName = services.getApplication();
            applicationName = nameService.getReplaceUnwantedCharacters(applicationName, false);
            path = fileUtils.createDirectory(basepath, path, dirsCreate, applicationName);

            for (Map.Entry entry : services.getServices().entrySet()) {
                String serviceName = (String) entry.getKey();
                logger().warn("Servicename: {}_{}", applicationName, serviceName);
                ContextSpec.ContextBuilder contextBuilder = ContextSpec.builder()
                        .withOrderPrio(Integer.parseInt(services.getOrderPrio()))
                        .withColorName(DomainColorMapper.getByType(services.getColor()))
                        .withIntegrationType(services.getIntegrationType())
                        .withApplicationName(services.getApplication());
                path = processServices(basepath, path, dirsCreate, services, applicationName, serviceName, contextBuilder);

                String[] interfaces = (String[]) entry.getValue();
                exampleFile.append(processInterface(Template.COMPONENT, path, contextBuilder, interfaces));
            }
            exampleFile.append(END.getContent());
            fileUtils.writeExampleFile(basepath, applicationName, exampleFile);
            fileUtils.writeEmptyCommonFile(basepath);
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
            exampleFile.append(START.getContent());
            exampleFile.append(DATE.getContent()).append(LocalDate.now())
                    .append(EOL.getContent());
            exampleFile.append(SEQUENCE_HEADER.getContent());
            // Prepare example file for every Application

            String applicationName = services.getApplication();
            applicationName = nameService.getReplaceUnwantedCharacters(applicationName, false);
            path = fileUtils.createDirectory(basepath, path, dirsCreate, applicationName);

            ContextSpec.ContextBuilder contextBuilder = ContextSpec.builder()
                    .withOrderPrio(Integer.parseInt(services.getOrderPrio()))
                    .withColorName(DomainColorMapper.getByType(services.getColor()))
                    .withIntegrationType(services.getIntegrationType())
                    .withApplicationName(services.getApplication());

            for (Map.Entry entry : services.getServices().entrySet()) {
                String serviceName = (String) entry.getKey();
                logger().warn("Servicename: {}_{}", applicationName, serviceName);
                path = processServices(basepath, path, dirsCreate, services, applicationName, serviceName, contextBuilder);

                String[] interfaces = (String[]) entry.getValue();
                exampleFile.append(processInterface(Template.SEQUENCE, path, contextBuilder, interfaces));

            }
            exampleFile.append(END.getContent());
            fileUtils.writeExampleFile(basepath, applicationName, exampleFile);
            fileUtils.writeEmptyCommonFile(basepath);
        }
    }

    private String processServices(String basePath, String path, Set<String> dirsCreate, Services services, String applicationName, String serviceName, ContextSpec.ContextBuilder contextBuilder) throws IOException {
        boolean isRest = decisionService.isCurrentServiceARestService(services);
        if (EMPTY.getValue().equalsIgnoreCase(serviceName)) {
            contextBuilder.withCommonPath(DIR_UP.getValue());
        } else {
            logger().info("Handle {} {} service", applicationName, serviceName);
            serviceName = nameService.getReplaceUnwantedCharacters(serviceName, true);
            if (!dirsCreate.contains(applicationName + serviceName)) {
                path = fileUtils.createServiceDirectory(basePath, dirsCreate, applicationName, serviceName);
            }
            contextBuilder.withServiceName(nameService.formatServiceName(serviceName, isRest));
            contextBuilder.withCommonPath(fileUtils.getRelativeCommonPath(serviceName));
        }
        return path;
    }

    private StringBuilder processInterface(final Template template, String path, ContextSpec.ContextBuilder contextBuilder, String[] interfaces) {
        StringBuilder exampleFile = new StringBuilder();
        if (interfaces == null) {
            return exampleFile;
        }
        // Pull context to change it later with workaround
        Context context = contextBuilder.getContext();

        for (String interfaceName : interfaces) {
            String currentPath = path;
            //first create the parent dir and next replace chars
            if (interfaceName.contains(SLASH.getValue())) {
                fileUtils.createParentDir(currentPath + interfaceName + FILE_TYPE_IUML.getValue());
                currentPath = path + interfaceName.split(SLASH.getValue())[0] + SLASH.getValue();

            }
            interfaceName = nameService.getReplaceUnwantedCharacters(interfaceName, false);
            //interface
            contextBuilder.withInterfaceName(interfaceName);
            exampleFile.append(fileUtils.writeInterfaceFile(template, currentPath, context));
        }
        return exampleFile;
    }


}