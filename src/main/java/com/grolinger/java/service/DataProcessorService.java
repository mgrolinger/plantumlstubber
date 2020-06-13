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

    Context processContextOfApplication(String colorName, String integrationType, String applicationName, String serviceName, String interfaceName, Integer orderPrio);

    void processApplication(final List<ApplicationDefinition> applicationList, final DiagramType diagramType) throws IOException;

    void exportTemplate();
}
