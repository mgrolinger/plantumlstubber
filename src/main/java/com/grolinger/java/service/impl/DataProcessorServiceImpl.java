package com.grolinger.java.service.impl;

import com.grolinger.java.config.Loggable;
import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.controller.templatemodel.Template;
import com.grolinger.java.controller.templatemodel.TemplateContent;
import com.grolinger.java.service.adapter.FileService;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.export.ComponentFile;
import com.grolinger.java.service.data.export.ExampleFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.grolinger.java.controller.templatemodel.TemplateContent.COMPONENTV2_HEADER;
import static com.grolinger.java.controller.templatemodel.TemplateContent.SEQUENCEV2_HEADER;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataProcessorServiceImpl implements Loggable, com.grolinger.java.service.DataProcessorService {
    private final FileService fileService;

    @Override
    public Context processContextOfApplication(String colorName, String integrationType, String systemType, String applicationName, String serviceName, String interfaceName, Integer orderPrio) {
        ServiceDefinition serviceDefinition = new ServiceDefinition(applicationName, serviceName, systemType, colorName, orderPrio);
        return new ContextSpec().builder()
                .withColorName(colorName)
                .withApplicationName(serviceDefinition.getApplicationName())
                .withServiceDefinition(serviceDefinition)
                .withOrderPrio(orderPrio)
                .withCommonPath(fileService.getRelativeCommonPath(applicationName, serviceName, interfaceName))
                .getContext();
    }

    @Override
    public void processApplication(List<ApplicationDefinition> pumlComponents, DiagramType diagramType) throws IOException {
        Map<String, String> dirsCreate = new HashMap<>();
        ComponentFile componentFile = new ComponentFile(diagramType);
        for (ApplicationDefinition pumlComponent : pumlComponents) {
            // Prepare example file for every Application
            ExampleFile exampleFile = new ExampleFile(getTemplate(diagramType), getTemplateContent(diagramType));
            for (ServiceDefinition serviceDefinition : pumlComponent.getServiceDefinitions()) {
                fileService.createDirectory(diagramType.getBasePath(), "", dirsCreate, serviceDefinition.getApplicationName());

                String path = "";
                componentFile.addComponent(serviceDefinition);
                ContextSpec.ContextBuilder contextBuilder = new ContextSpec()
                        .builder()
                        .withColorName(serviceDefinition.getDomainColor())
                        .withApplicationName(serviceDefinition.getApplicationName())
                        .withServiceDefinition(serviceDefinition)
                        .withCustomAlias(pumlComponent.getCustomAlias())
                        .withOrderPrio(serviceDefinition.getOrderPrio());

                path = createDirectoryForService(diagramType.getBasePath(), dirsCreate, serviceDefinition);

                processInterfaces(path, contextBuilder, serviceDefinition, exampleFile);
            }
            fileService.writeExampleFile(diagramType.getBasePath(), pumlComponent.getName(), exampleFile.getFullFileContent());
        }
        fileService.writeDefaultCommonFile(diagramType.getBasePath(), diagramType);
        fileService.writeComponentFile(diagramType, componentFile);

    }

    private String createDirectoryForService(String basePath, Map<String, String> dirsCreate, ServiceDefinition serviceDefinition) throws IOException {
        logger().info("Processing service:{}_{}", serviceDefinition.getApplicationName(), serviceDefinition.getServiceCallName());
        String pathForReturnValue;

        if (!dirsCreate.containsKey(serviceDefinition.getApplicationName() + serviceDefinition.getServiceCallName())) {
            // create directory if not done yet
            String path = fileService.createServiceDirectory(basePath, serviceDefinition);
            dirsCreate.put(serviceDefinition.getApplicationName() + serviceDefinition.getServiceCallName(), path);
        }
        pathForReturnValue = dirsCreate.get(serviceDefinition.getApplicationName() + serviceDefinition.getServiceCallName());

        return pathForReturnValue;
    }

    private void processInterfaces(String path, ContextSpec.ContextBuilder contextBuilder, ServiceDefinition serviceDefinition, ExampleFile exampleFile) {
        logger().info("Current path: {}", path);
        for (InterfaceDefinition currentInterface : serviceDefinition.getInterfaceDefinitions()) {
            if (currentInterface.hasRelativeCommonPath()) {
                contextBuilder.addToCommonPath(currentInterface.getRelativeCommonPath());
            }
            // ignore call stack information
            logger().info("Extracted interface: {}", currentInterface.getName());
            if (currentInterface.containsPath()) {
                //first create the parent dir and next replace chars
                fileService.createParentDir(path + currentInterface.getName());
            }
            contextBuilder.withInterfaceDefinition(currentInterface);

            // Pull context to use it later for export
            exampleFile = fileService.writeInterfaceFile(path, serviceDefinition, currentInterface, contextBuilder.getContext(), exampleFile);
        }

    }

    private Template getTemplate(final DiagramType diagramType) {
        if (DiagramType.COMPONENT_DIAGRAM_BASE.equals(diagramType)) {
            return Template.COMPONENT_V2;
        }
        return Template.SEQUENCE_V2;

    }

    private TemplateContent getTemplateContent(final DiagramType diagramType) {
        if (DiagramType.COMPONENT_DIAGRAM_BASE.equals(diagramType)) {
            return COMPONENTV2_HEADER;
        }
        return SEQUENCEV2_HEADER;

    }

}
