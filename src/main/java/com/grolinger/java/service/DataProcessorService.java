package com.grolinger.java.service;

import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.data.ApplicationDefinition;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.List;

/**
 * Service Processor
 */
public interface DataProcessorService {

    /**
     * Processes the application specified from the interface
     *
     * @param colorName
     * @param integrationType
     * @param applicationName
     * @param serviceName
     * @param interfaceName
     * @param orderPrio
     * @return
     * @deprecated since 01.08.2020
     */
    Context processContextOfApplication(String colorName, String integrationType, String applicationName, String serviceName, String interfaceName, Integer orderPrio);

    /**
     * Processes the application list previously loaded from the filesystem.
     *
     * @param applicationList imported specifications
     * @param diagramType     kind of diagram that will be exported
     * @throws IOException if the plantumlstubber cannot write to the local file system
     */
    void processApplication(final List<ApplicationDefinition> applicationList, final DiagramType diagramType) throws IOException;

    /**
     * Exports the yaml template to specify an application.
     */
    void exportTemplate();
}
