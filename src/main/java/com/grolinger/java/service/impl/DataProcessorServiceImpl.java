package com.grolinger.java.service.impl;

import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.DataProcessorService;
import com.grolinger.java.service.adapter.exportdata.LocalExportAdapter;
import com.grolinger.java.service.adapter.exportdata.LocalStaticAdapter;
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
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataProcessorServiceImpl implements DataProcessorService {
    private final LocalExportAdapter localExportAdapter;
    private final LocalStaticAdapter localStaticAdapter;

    @Override
    public Context processContextOfApplication(String colorName, String integrationType, String applicationName, String serviceName, String interfaceName, Integer orderPrio) {
        // Fixme: missing all new features for multi-service
        ApplicationDefinition applicationDefinition = ApplicationDefinition.builder()
                .name(applicationName)
                .alias(applicationName.toLowerCase())
                .label(applicationName)
                .orderPrio(orderPrio)
                .build();
        final InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder()
                .originalInterfaceName(interfaceName)
                .applicationDomainColor(colorName)
                .integrationType(integrationType)
                .build();
        ServiceDefinition serviceDefinition = ServiceDefinition.builder()
                .serviceName(serviceName)
                .domainColor(colorName)
                .interfaceDefinitions(Collections.singletonList(interfaceDefinition))
                .build();

        return new ContextSpec().builder()
                .withColorName(colorName)
                .withApplication(applicationDefinition)
                .withServiceDefinition(serviceDefinition)
                .withInterfaceDefinition(interfaceDefinition)
                .withOrderPrio(orderPrio)
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
        ComponentFile componentFile = new ComponentFile(diagramType);
        for (ApplicationDefinition currentApplication : pumlComponents) {
            // Prepare example file for every Application
            ExampleFile exampleFile = new ExampleFile(diagramType);
            for (ServiceDefinition serviceDefinition : currentApplication.getServiceDefinitions()) {
                localExportAdapter.createDirectory(diagramType.getBasePath(), "", currentApplication);

                String path = "";
                componentFile.addComponent(currentApplication, serviceDefinition);
                ContextSpec.ContextBuilder contextBuilder = new ContextSpec()
                        .builder()
                        .withColorName(serviceDefinition.getDomainColor())
                        .withApplication(currentApplication)
                        .withServiceDefinition(serviceDefinition)
                        .withOrderPrio(currentApplication.getOrderPrio());

                path = createDirectoryForService(diagramType.getBasePath(), currentApplication, serviceDefinition);

                processInterfaces(path, contextBuilder, currentApplication, serviceDefinition, exampleFile);
            }
            localExportAdapter.writeExampleFile(diagramType.getBasePath(), currentApplication, exampleFile.getFullFileContent());
        }
        localExportAdapter.writeComponentFile(diagramType, componentFile);
        // Write everything connected to common.iuml and common/
        localStaticAdapter.writeDefaultCommonFile(diagramType.getBasePath(), diagramType);
        // Write the skin files
        localStaticAdapter.writeDefaultSkinFiles();
    }

    @Override
    public void exportTemplate() {
        localStaticAdapter.exportTemplate();
    }

    /**
     * Creates directories for the services
     *
     * @param basePath              the path in which the folder should be created
     * @param applicationDefinition application
     * @param serviceDefinition     service
     * @return Path of the created directory
     * @throws IOException may cause problems during creation of a directory
     */
    private String createDirectoryForService(String basePath, ApplicationDefinition applicationDefinition, ServiceDefinition serviceDefinition) throws IOException {
        log.info("Create directory for application {} and service {}", applicationDefinition.getPath(), serviceDefinition.getPath());
        return localExportAdapter.createServiceDirectory(basePath, applicationDefinition, serviceDefinition);
    }

    private void processInterfaces(String path, ContextSpec.ContextBuilder contextBuilder, final ApplicationDefinition currentApplication, final ServiceDefinition currentService, ExampleFile exampleFile) {
        log.info("Current path: {}", path);
        for (InterfaceDefinition currentInterface : currentService.getInterfaceDefinitions()) {
            // ignore call stack information
            log.info("Extracted interface: {}", currentInterface.getPath());
            if (currentInterface.containsPath()) {
                //first create the parent dir and next replace chars
                localExportAdapter.createParentDir(path + currentInterface.getPath());
            }
            contextBuilder.withInterfaceDefinition(currentInterface);
            //Todo find a better solution, but for now we need to set the complete path every time we process an interface
            contextBuilder.withCommonPath(currentApplication.getPathToRoot() + currentService.getPathToRoot() + currentInterface.getPathToRoot());

            // Pull context to use it later for export
            exampleFile = localExportAdapter.writeInterfaceFile(path, currentApplication, currentService, currentInterface, contextBuilder.getContext(), exampleFile);
        }

    }
}
