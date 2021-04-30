package com.grolinger.java.service;

import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.data.ApplicationDefinition;

import java.io.IOException;
import java.util.List;

/**
 * Service Processor
 */
public interface DataProcessorService {

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
