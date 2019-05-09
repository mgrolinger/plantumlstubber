package com.grolinger.java.controller;

import com.grolinger.java.config.Loggable;
import com.grolinger.java.config.Services;
import com.grolinger.java.service.DecisionService;
import com.grolinger.java.service.FileService;
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
    private final FileService fileService;
    private NameService nameService;
    private DecisionService decisionService;
    private static final String GLOBAL_FILE_EXPORT_PATH="./target/";


    @Autowired
    private MultiExportController(FileService fileService, NameService nameService, DecisionService decisionService) {
        this.fileService = fileService;
        this.nameService = nameService;
        this.decisionService = decisionService;

    }

    @GetMapping("/export/components")
    public void component(Model model) throws IOException {
        // Find all yaml files
        List<Services> applicationList = new LinkedList<>(fileService.findAllYamlFiles());

        // Iterate over yaml files
        String basepath = GLOBAL_FILE_EXPORT_PATH+"Component/";
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
            applicationName = nameService.replaceUnwantedCharacters(applicationName, false);
            path = fileService.createDirectory(basepath, path, dirsCreate, applicationName);

            for (Map.Entry entry : services.getServices().entrySet()) {
                String serviceName = (String) entry.getKey();
                logger().warn("Servicename: {}_{}", applicationName, serviceName);
                ContextSpec.ContextBuilder contextBuilder = new ContextSpec().builder()
                        .withOrderPrio(Integer.parseInt(services.getOrderPrio()))
                        .withColorName(DomainColorMapper.getByType(services.getColor()))
                        .withIntegrationType(services.getIntegrationType())
                        .withApplicationName(services.getApplication());
                path = processServices(basepath, path, dirsCreate, services, applicationName, serviceName, contextBuilder);

                String[] interfaces = (String[]) entry.getValue();
                exampleFile.append(processInterface(Template.COMPONENT, path, contextBuilder, interfaces));
            }
            exampleFile.append(END.getContent());
            fileService.writeExampleFile(basepath, applicationName, exampleFile);
            fileService.writeEmptyCommonFile(basepath);
        }
    }

    @GetMapping("/export/sequences")
    public void sequence(Model model) throws IOException {
        // Find all yaml files
        List<Services> applicationList = new LinkedList<>(fileService.findAllYamlFiles());

        // Iterate over yaml files
        String basepath = GLOBAL_FILE_EXPORT_PATH+"Sequence/";
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
            applicationName = nameService.replaceUnwantedCharacters(applicationName, false);
            path = fileService.createDirectory(basepath, path, dirsCreate, applicationName);

            ContextSpec.ContextBuilder contextBuilder = new ContextSpec().builder()
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
            fileService.writeExampleFile(basepath, applicationName, exampleFile);
            fileService.writeEmptyCommonFile(basepath);
        }
    }

    private String processServices(String basePath, String path, Set<String> dirsCreate, Services services, String applicationName, String serviceName, ContextSpec.ContextBuilder contextBuilder) throws IOException {
        boolean isRest = decisionService.isCurrentServiceARestService(services);
        contextBuilder.withPreformattedServiceName(nameService.formatServiceName(serviceName, isRest));
        logger().info("Handle {} {} service", applicationName, serviceName);

        if (EMPTY.getValue().equalsIgnoreCase(serviceName)) {
            contextBuilder.withCommonPath(DIR_UP.getValue());
        } else {
            serviceName = nameService.replaceUnwantedCharacters(serviceName, true);
            if (!dirsCreate.contains(applicationName + serviceName)) {
                // create directory if not done yet
                path = fileService.createServiceDirectory(basePath, dirsCreate, applicationName, serviceName);
            }
            contextBuilder.withCommonPath(fileService.getRelativeCommonPath(serviceName));
        }
        return path;
    }

    private StringBuilder processInterface(final Template template, String path, ContextSpec.ContextBuilder contextBuilder, String[] interfaces) {
        StringBuilder exampleFile = new StringBuilder();
        if (interfaces == null) {
            return exampleFile;
        }

        for (String interfaceName : interfaces) {
            String currentPath = path;
            //first create the parent dir and next replace chars
            if (interfaceName.contains(SLASH.getValue())) {
                fileService.createParentDir(currentPath + interfaceName + FILE_TYPE_IUML.getValue());
                currentPath = path + interfaceName.split(SLASH.getValue())[0] + SLASH.getValue();

            }
            interfaceName = nameService.replaceUnwantedCharacters(interfaceName, false);
            //interface
            contextBuilder.withInterfaceName(interfaceName);
            // Pull context to change it later with workaround
            Context context = contextBuilder.getContext();
            exampleFile.append(fileService.writeInterfaceFile(template, currentPath, context));
        }
        return exampleFile;
    }


}