package com.grolinger.java.service.impl;

import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.DataProcessorService;
import com.grolinger.java.service.adapter.FileService;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.exportdata.ComponentFile;
import com.grolinger.java.service.data.exportdata.ExampleFile;
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
                //.withCommonPath(fileService.getRelativeCommonPath(applicationName, serviceName, interfaceName))
                .getContext();
    }

    /**
     * Processes all applications that are defined by a list of {@link ApplicationDefinition},
     * {@link ServiceDefinition} and {@link InterfaceDefinition}. The result is exported as diagrams
     * specified by {@link DiagramType}.
     *
     * @param pumlComponents The applications, services and interfaces defined in a tree structure
     * @param diagramType    either {@code Component} or {@code Sequence}
     * @throws IOException Exception if the files cannot be written to the local file system
     */
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
    }

    /**
     * Creates directories for the services
     *
     * @param basePath
     * @param dirsCreate
     * @param applicationDefinition
     * @param serviceDefinition
     * @return
     * @throws IOException
     */
    private String createDirectoryForService(String basePath, Map<String, String> dirsCreate, ApplicationDefinition applicationDefinition, ServiceDefinition serviceDefinition) throws IOException {
        log.info("Create directory for application {} and service {}", applicationDefinition.getName(), serviceDefinition.getPath());
        String pathForReturnValue;

        if (!dirsCreate.containsKey(applicationDefinition.getName() + serviceDefinition.getPath())) {
            // create directory if not done yet
            String path = fileService.createServiceDirectory(basePath, applicationDefinition, serviceDefinition);
            dirsCreate.put(applicationDefinition.getName() + serviceDefinition.getPath(), path);
        }
        pathForReturnValue = dirsCreate.get(applicationDefinition.getName() + serviceDefinition.getPath());

        return pathForReturnValue;
    }

    private void processInterfaces(String path, ContextSpec.ContextBuilder contextBuilder, final ApplicationDefinition currentApplication, final ServiceDefinition currentService, ExampleFile exampleFile) {
        log.info("Current path: {}", path);
        for (InterfaceDefinition currentInterface : currentService.getInterfaceDefinitions()) {
            //contextBuilder.addToCommonPath(currentInterface.getPathToRoot());
            // ignore call stack information
            log.info("Extracted interface: {}", currentInterface.getPath());
            if (currentInterface.containsPath()) {
                //first create the parent dir and next replace chars
                fileService.createParentDir(path + currentInterface.getPath());
            }
            contextBuilder.withInterfaceDefinition(currentInterface);
            //Todo find a better solution, but for now we need to set the complete path every time we process an interface
            contextBuilder.withCommonPath(currentApplication.getPathToRoot() + currentService.getPathToRoot() + currentInterface.getPathToRoot());

            // Pull context to use it later for export
            exampleFile = fileService.writeInterfaceFile(path, currentApplication, currentService, currentInterface, contextBuilder.getContext(), exampleFile);
        }

    }
}
