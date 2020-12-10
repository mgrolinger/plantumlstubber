package com.grolinger.java.service.data.exportdata;

import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.mapper.ColorMapper;
import com.grolinger.java.service.impl.ColorGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.grolinger.java.controller.templatemodel.Constants.COMPONENT_SUFFIX;
import static com.grolinger.java.controller.templatemodel.TemplateContent.*;

/**
 * Exporter for the automatic component/participant initialization from
 * either component_definition.iuml or participant_definition.iuml
 */
@Slf4j
public class ComponentFile {
    private final DiagramType diagramType;
    private final StringBuilder content;
    private StringBuilder bucket1;
    private StringBuilder bucket2;
    private final Set<String> doneApplication;

    @NotNull
    public ComponentFile(final DiagramType diagramType) {
        this.diagramType = diagramType;
        this.doneApplication = new HashSet<>();
        content = new StringBuilder();

        content.append(START.getContent());
        content.append(DATE.getContent()).append(LocalDate.now())
                .append(CARRIAGE_RETURN.getContent());

        if (DiagramType.COMPONENT_V1_2019_6_DIAGRAM_BASE.equals(diagramType) || DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE.equals(diagramType)) {
            // Component diagrams need two buckets
            bucket1 = new StringBuilder();
            bucket1.append("!if ($UML_STRICT == %true())")
                    .append(CARRIAGE_RETURN.getContent());
            bucket2 = new StringBuilder().append(CARRIAGE_RETURN.getContent());
            bucket2.append("!else").append(CARRIAGE_RETURN.getContent())
                    .append("  'use tupadr3/common and tupadr3/font-awesome").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/common>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/font-awesome/server>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/font-awesome/database>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/font-awesome/archive>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/font-awesome/address_book>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/font-awesome/file_o>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/font-awesome/file_zip_o>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/font-awesome/envelope>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/font-awesome/users>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/font-awesome/user>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/font-awesome/folder>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/devicons/jenkins>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include <tupadr3/devicons/terminal>").append(CARRIAGE_RETURN.getContent())
                    .append("  !include devicons/springBoot.puml").append(CARRIAGE_RETURN.getContent())
                    .append("  !include devicons/solr.puml").append(CARRIAGE_RETURN.getContent())
                    .append("  !include devicons/sap.puml").append(CARRIAGE_RETURN.getContent())
                    .append("  !include devicons/salesforce.puml").append(CARRIAGE_RETURN.getContent())
                    .append(CARRIAGE_RETURN.getContent());

        } else {
            content.append(CARRIAGE_RETURN.getContent())
                    .append(getEnableOrderingDefault()).append(CARRIAGE_RETURN.getContent())
                    .append(getDefaultSequenceHeader()).append(CARRIAGE_RETURN.getContent())
                    .append(CARRIAGE_RETURN.getContent());
        }
    }

    /**
     * Adds a component to the current file
     * @param currentApplication the current application
     * @param currentService the current service
     */
    public void addComponent(final ApplicationDefinition currentApplication, final ServiceDefinition currentService) {
        if (!doneApplication.contains(currentApplication.getName())) {
            if (DiagramType.COMPONENT_V1_2019_6_DIAGRAM_BASE.equals(diagramType) || DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE.equals(diagramType)) {
                final String componentName = Constants.DEFINE_FUNCTION_PREFIX.getValue() +
                        currentApplication.getName().toUpperCase() + COMPONENT_SUFFIX.getValue();

                content.append(CARRIAGE_RETURN.getContent());
                // UMLStrict
                bucket1.append("  ").append(componentName).append(" = '").append(currentApplication.getSystemType().getUmlStrict())
                        .append(" \"").append(currentApplication.getLabel())
                        .append("\" as ")
                        .append(currentApplication.getAlias()).append(" ")
                        .append(ColorMapper.getStereotype(currentService.getDomainColor())).append("'")
                        .append(CARRIAGE_RETURN.getContent());
                log.info("Feature not yet used; Color schema: {} -> {}",
                        currentService.getDomainColor(),
                        ColorGenerator.getColorCode(currentService.getDomainColor()));
                // Font-awesome
                //TODO systeme unterscheiden
                bucket2.append("  ").append(componentName).append(" = ").append(currentApplication.getSystemType().getFontAwesome()).append("(\"")
                        .append(currentApplication.getAlias()).append("\",\"")
                        .append(currentApplication.getLabel()).append("\") + '")
                        .append(ColorMapper.getStereotype(currentService.getDomainColor())).append("'")
                        .append(CARRIAGE_RETURN.getContent());
                doneApplication.add(currentApplication.getName());
            } else {
                String participantName = currentApplication.getName();
                // Generate default
                content.append("!if (%not(%variable_exists(\"").append(Constants.FUNCTION_V2_PREFIX.getValue()).append(participantName.toUpperCase()).append("_ORDER_PRIO\")))")
                        .append(CARRIAGE_RETURN.getContent())
                        .append("     ")
                        .append(Constants.DEFINE_FUNCTION_PREFIX.getValue()).append(participantName.toUpperCase())
                        .append("_ORDER_PRIO = $getOrder(").append(currentApplication.getOrderPrio()).append(")").append(CARRIAGE_RETURN.getContent())
                        .append("!endif")
                        .append(CARRIAGE_RETURN.getContent());
                //define the participant
                content.append(Constants.DEFINE_FUNCTION_PREFIX.getValue()).append(currentApplication.getName().toUpperCase())
                        .append(Constants.PARTICIPANT_SUFFIX.getValue()).append(" = '").append(currentApplication.getSystemType().getSequenceName()).append(" \"")
                        .append(currentApplication.getLabel()).append("\" as ")
                        .append(currentApplication.getAlias()).append(ColorMapper.getStereotype(currentService.getDomainColor())).append("' + ")
                        .append(Constants.FUNCTION_V2_PREFIX.getValue()).append(participantName.toUpperCase()).append("_ORDER_PRIO")
                        .append(CARRIAGE_RETURN.getContent()).append(CARRIAGE_RETURN.getContent());

                doneApplication.add(currentApplication.getName());
            }
        }
    }

    /**
     * Copies the content of a example file and syntactically closes the plantUML example.
     *
     * @return example file content as String
     */
    public String getFullFileContent() {
        StringBuilder copy;
        if (DiagramType.COMPONENT_V1_2019_6_DIAGRAM_BASE.equals(diagramType) || DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE.equals(diagramType)) {
            // bucket1 is closed by bucket2 automatically
            // bucket2 close with endif
            bucket2.append(CARRIAGE_RETURN.getContent()).append("!endif").append(CARRIAGE_RETURN.getContent());
            copy = new StringBuilder(bucket1.capacity() + bucket2.capacity());
            copy.append(bucket1);
            copy.append(bucket2);
        } else {
            copy = content;
        }

        // close the file
        content.append(END.getContent());
        return copy.toString();
    }

    private String getDefaultSequenceHeader() {
        return "'' !$ENABLE_FEATURE_ORDERING = %true() allows to override the ordering of participant" + CARRIAGE_RETURN.getContent() +
                "'' which is the order of creation by default" + CARRIAGE_RETURN.getContent() +
                "!unquoted function $getOrder($ORDER_PRIO)" + CARRIAGE_RETURN.getContent() +
                "  !if ($ENABLE_FEATURE_ORDERING)" + CARRIAGE_RETURN.getContent() +
                "    !return ' order '+$ORDER_PRIO" + CARRIAGE_RETURN.getContent() +
                "  !else" + CARRIAGE_RETURN.getContent() +
                "    !return ' '" + CARRIAGE_RETURN.getContent() +
                "  !endif" + CARRIAGE_RETURN.getContent() +
                "!endfunction" + CARRIAGE_RETURN.getContent();
    }

    private String getEnableOrderingDefault() {
        return "!if (%not(%variable_exists(\"$ENABLE_FEATURE_ORDERING\")))" + CARRIAGE_RETURN.getContent() +
                "  !$ENABLE_FEATURE_ORDERING = %false()" + CARRIAGE_RETURN.getContent() +
                "!endif" + CARRIAGE_RETURN.getContent();
    }
}
