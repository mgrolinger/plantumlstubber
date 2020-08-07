package com.grolinger.java.service.data.exportdata;

import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.controller.templatemodel.Template;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static com.grolinger.java.controller.templatemodel.TemplateContent.*;
import static com.grolinger.java.service.adapter.exportdata.impl.LocalExportAdapterImpl.FILE_TYPE_IUML;

/**
 * Exporter for the example file that is generated for every application to ease
 * the visual check of new services. It can be found in the root folder of each
 * application with <name_of_application>_example.puml
 */
@Slf4j
public class ExampleFile {
    private final Template template;
    private final StringBuilder content;

    /**
     * Definition of the example file for an application and diagramType
     *
     * @param diagramType defines the diagram type for the example file
     */
    @NotNull
    public ExampleFile(final DiagramType diagramType) {
        this.template = diagramType.getTemplate();

        content = new StringBuilder();
        content.append(START.getContent());
        content.append(DATE.getContent()).append(LocalDate.now())
                .append(CARRIAGE_RETURN.getContent());
        content.append(diagramType.getTemplateContent().getContent());
    }

    /**
     * Adds an !include service/interface.iuml
     *
     * @param currentService   the current service
     * @param currentInterface the current interface
     */
    public void addInclude(final ServiceDefinition currentService, final InterfaceDefinition currentInterface) {
        content.append(INCLUDE.getContent())
                .append(currentService.getPath())
                .append(currentInterface.getPath())
                .append(FILE_TYPE_IUML)
                .append(CARRIAGE_RETURN.getContent());
    }

    /**
     * Adds a !function call to the example file that joins all parameters by _
     * e.g. $currentApplication_currentService_currentInterface("consumer")
     *
     * @param currentApplication the current application
     * @param currentService     the current service
     * @param currentInterface   the current interface
     */
    public void addFunction(final ApplicationDefinition currentApplication,
                            final ServiceDefinition currentService,
                            final InterfaceDefinition currentInterface) {

        content.append(Constants.FUNCTION_V2_PREFIX.getValue())
                .append(StringUtils.capitalize(currentApplication.getName()))
                .append(StringUtils.isEmpty(currentApplication.getName()) ? "" : Constants.NAME_SEPARATOR.getValue())
                .append(currentService.getServiceCallName())
                //append separator only when service is not EMPTY otherwise we end up in double underscore App__interface() instead of App_interface()
                .append(StringUtils.isEmpty(currentService.getServiceCallName()) ? "" : Constants.NAME_SEPARATOR.getValue())
                .append(currentInterface.getCallName())
                .append("(\"")
                .append("consumer").append(currentApplication.getAlias().toLowerCase())
                .append("\")")
                .append(CARRIAGE_RETURN.getContent()).append(CARRIAGE_RETURN.getContent());
        log.info("Write {}_{}_{} to {}{}", currentApplication.getName(),
                currentService.getServiceCallName(),
                currentInterface.getCallName(),
                currentInterface.getMethodName(),
                FILE_TYPE_IUML);
    }

    /**
     * Returns the current template
     *
     * @return template
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Copies the content of a example file and syntactically closes the plantUML example.
     * This does not influence the content container.
     *
     * @return example file content as String
     */
    public String getFullFileContent() {
        return content.toString() + END.getContent();
    }
}
