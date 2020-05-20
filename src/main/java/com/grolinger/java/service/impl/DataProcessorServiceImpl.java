package com.grolinger.java.service.impl;

import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.DataProcessorService;
import com.grolinger.java.service.adapter.FileService;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.export.ComponentFile;
import com.grolinger.java.service.data.export.ExampleFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataProcessorServiceImpl implements DataProcessorService {
    private final FileService fileService;

    @Override
    public Context processContextOfApplication(String colorName, String integrationType, String applicationName, String serviceName, String interfaceName, Integer orderPrio) {
        // Fixme: missing all new features for multi-service
        ApplicationDefinition applicationDefinition = ApplicationDefinition.builder()
                .name(applicationName).alias(applicationName.toLowerCase()).label(applicationName)
                .orderPrio(orderPrio)
                .build();
        ServiceDefinition serviceDefinition = ServiceDefinition.builder()
                .serviceName(serviceName)
                .domainColor(colorName)

                .build();

        return new ContextSpec().builder()
                .withColorName(colorName)
                .withApplication(applicationDefinition)
                .withServiceDefinition(serviceDefinition)
                .withOrderPrio(orderPrio)
                .withCommonPath(fileService.getRelativeCommonPath(applicationName, serviceName, interfaceName))
                .getContext();
    }

    @Override
    public void processApplication(List<ApplicationDefinition> pumlComponents, DiagramType diagramType) throws IOException {
        Map<String, String> dirsCreate = new HashMap<>();
        ComponentFile componentFile = new ComponentFile(diagramType);
        for (ApplicationDefinition currentApplication : pumlComponents) {
            // Prepare example file for every Application
            ExampleFile exampleFile = new ExampleFile(diagramType.getTemplate(), diagramType.getTemplateContent());
            for (ServiceDefinition serviceDefinition : currentApplication.getServiceDefinitions()) {
                fileService.createDirectory(diagramType.getBasePath(), "", dirsCreate, currentApplication.getName());

                String path = "";
                componentFile.addComponent(currentApplication, serviceDefinition);
                ContextSpec.ContextBuilder contextBuilder = new ContextSpec()
                        .builder()
                        .withColorName(serviceDefinition.getDomainColor())
                        .withApplication(currentApplication)
                        .withServiceDefinition(serviceDefinition)
                        .withOrderPrio(currentApplication.getOrderPrio());

                path = createDirectoryForService(diagramType.getBasePath(), dirsCreate, currentApplication, serviceDefinition);

                processInterfaces(path, contextBuilder, currentApplication, serviceDefinition, exampleFile);
            }
            fileService.writeExampleFile(diagramType.getBasePath(), currentApplication.getName(), exampleFile.getFullFileContent());
        }
        // Write the skin files
        fileService.writeDefaultSkinFiles();
        // Write everything connected to common.iuml and common/
        fileService.writeDefaultCommonFile(diagramType.getBasePath(), diagramType);
        fileService.writeComponentFile(diagramType, componentFile);
        //Todo: Add static import files here

    }

    private String createDirectoryForService(String basePath, Map<String, String> dirsCreate, ApplicationDefinition applicationDefinition, ServiceDefinition serviceDefinition) throws IOException {
        log.info("Processing service:{} {}", applicationDefinition.getName(), serviceDefinition.getServiceCallName());
        String pathForReturnValue;

        if (!dirsCreate.containsKey(applicationDefinition.getName() + serviceDefinition.getServiceCallName())) {
            // create directory if not done yet
            String path = fileService.createServiceDirectory(basePath, applicationDefinition, serviceDefinition);
            dirsCreate.put(applicationDefinition.getName() + serviceDefinition.getServiceCallName(), path);
        }
        pathForReturnValue = dirsCreate.get(applicationDefinition.getName() + serviceDefinition.getServiceCallName());

        return pathForReturnValue;
    }

    private void processInterfaces(String path, ContextSpec.ContextBuilder contextBuilder, final ApplicationDefinition currentApplication, final ServiceDefinition serviceDefinition, ExampleFile exampleFile) {
        log.info("Current path: {}", path);
        for (InterfaceDefinition currentInterface : serviceDefinition.getInterfaceDefinitions()) {
            if (currentInterface.hasRelativeCommonPath()) {
                contextBuilder.addToCommonPath(currentInterface.getRelativeCommonPath());
            }
            // ignore call stack information
            log.info("Extracted interface: {}", currentInterface.getName());
            if (currentInterface.containsPath()) {
                //first create the parent dir and next replace chars
                fileService.createParentDir(path + currentInterface.getName());
            }
            contextBuilder.withInterfaceDefinition(currentInterface);

            // Pull context to use it later for export
            exampleFile = fileService.writeInterfaceFile(path, currentApplication, serviceDefinition, currentInterface, contextBuilder.getContext(), exampleFile);
        }

    }
}
