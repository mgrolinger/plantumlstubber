package com.grolinger.java.service.data.export;

import com.grolinger.java.config.Loggable;
import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.mapper.ColorMapper;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.grolinger.java.controller.templatemodel.Constants.COMPONENT_SUFFIX;
import static com.grolinger.java.controller.templatemodel.TemplateContent.*;

/**
 * Exporter for the automatic component/participant initialization from
 * either component_definition.iuml or aprticipant_definition.iuml
 */
public class ComponentFile implements Loggable {
    private DiagramType diagramType;
    private StringBuilder content;
    private StringBuilder bucket1;
    private StringBuilder bucket2;
    private Set<String> doneApplication;


    @NotNull
    public ComponentFile(final DiagramType diagramType) {
        this.diagramType = diagramType;
        this.doneApplication = new HashSet<>();
        content = new StringBuilder();

        content.append(START.getContent());
        content.append(DATE.getContent()).append(LocalDate.now())
                .append(EOL.getContent());

        if (DiagramType.COMPONENT_DIAGRAM_BASE.equals(diagramType)) {
            // Component diagrams need two buckets
            bucket1 = new StringBuilder();
            bucket1.append("!if ($UML_STRICT == %true())")
                    .append(EOL.getContent());
            bucket2 = new StringBuilder().append(EOL.getContent());
            bucket2.append("!else").append(EOL.getContent())
                    .append("  'use tupadr3/common and tupadr3/font-awesome").append(EOL.getContent())
                    .append("  !include <tupadr3/common>").append(EOL.getContent())
                    .append("  !include <tupadr3/font-awesome/server>").append(EOL.getContent())
                    .append("  !include <tupadr3/font-awesome/database>").append(EOL.getContent())
                    .append("  !include <tupadr3/font-awesome/archive>").append(EOL.getContent())
                    .append("  !include <tupadr3/font-awesome/address_book>").append(EOL.getContent())
                    .append("  !include <tupadr3/font-awesome/file_o>").append(EOL.getContent())
                    .append("  !include <tupadr3/font-awesome/file_zip_o>").append(EOL.getContent())
                    .append("  !include <tupadr3/font-awesome/envelope>").append(EOL.getContent())
                    .append("  !include <tupadr3/font-awesome/users>").append(EOL.getContent())
                    .append("  !include <tupadr3/font-awesome/user>").append(EOL.getContent())
                    .append("  !include <tupadr3/font-awesome/folder>").append(EOL.getContent())
                    .append("  !include <tupadr3/devicons/jenkins>").append(EOL.getContent())
                    .append("  !include <tupadr3/devicons/terminal>").append(EOL.getContent())
                    .append("  !include devicons/springboot.puml").append(EOL.getContent())
                    .append("  !include devicons/btix.puml").append(EOL.getContent())
                    .append("  !include devicons/solr.puml").append(EOL.getContent())
                    .append(EOL.getContent());

        } else {
            content.append(EOL.getContent())
                    .append(getEnableOrderingDefault()).append(EOL.getContent())
                    .append(getDefaultSequenceHeader()).append(EOL.getContent())
                    .append(EOL.getContent());
        }
    }

    public void addComponent(final ServiceDefinition currentService) {
        if (!doneApplication.contains(currentService.getApplicationName())) {
            if (DiagramType.COMPONENT_DIAGRAM_BASE.equals(diagramType)) {
                final String componentName = Constants.DEFINE_FUNCTION_PREFIX.getValue() +
                        currentService.getApplicationName().toUpperCase() + COMPONENT_SUFFIX.getValue();

                content.append(EOL.getContent());
                // UMLStrict
                bucket1.append("  ").append(componentName).append(" = '").append(currentService.getSystemType().getUmlStrict()).append(" \"").append(currentService.getApplicationName()).append("\" as ")
                        .append(currentService.getApplicationName().toLowerCase()).append(" ").append(ColorMapper.getStereotype(currentService.getDomainColor())).append("'").append(EOL.getContent());
                // Font-awesome
                //TODO systeme unterscheiden
                //Todo auf Template umstellen
                bucket2.append("  ").append(componentName).append(" = ").append(currentService.getSystemType().getFontAwesome()).append("(\"").append(currentService.getApplicationName().toLowerCase()).append("\",\"").append(currentService.getApplicationName())
                        .append("\") + '").append(ColorMapper.getStereotype(currentService.getDomainColor())).append("'").append(EOL.getContent());
                doneApplication.add(currentService.getApplicationName());
            } else {
                String participantName = currentService.getApplicationName();
                // Generate default
                content.append("!if (%not(%variable_exists(\"").append(Constants.FUNCTION_V2_PREFIX.getValue()).append(participantName.toUpperCase()).append("_ORDER_PRIO\")))")
                        .append(EOL.getContent())
                        .append("     ")
                        .append(Constants.DEFINE_FUNCTION_PREFIX.getValue()).append(participantName.toUpperCase())
                        .append("_ORDER_PRIO = $getOrder(").append(currentService.getOrderPrio()).append(")").append(EOL.getContent())
                        .append("!endif")
                        .append(EOL.getContent());
                //define the participant
                content.append(Constants.DEFINE_FUNCTION_PREFIX.getValue()).append(currentService.getApplicationName().toUpperCase())
                        .append(Constants.PARTICIPANT_SUFFIX.getValue()).append(" = '").append(currentService.getSystemType().getSequenceName()).append(" \"").append(currentService.getApplicationName()).append("\" as ")
                        .append(currentService.getApplicationName().toLowerCase()).append(ColorMapper.getStereotype(currentService.getDomainColor())).append("' + ")
                        .append(Constants.FUNCTION_V2_PREFIX.getValue()).append(participantName.toUpperCase()).append("_ORDER_PRIO")
                        .append(EOL.getContent()).append(EOL.getContent());

                doneApplication.add(currentService.getApplicationName());
            }
        } else {
            logger().debug("{} is already defined.", currentService.getApplicationName());
        }

    }

    /**
     * Copies the content of a example file and syntactically closes the plantUML example.
     *
     * @return example file content as String
     */
    public String getFullFileContent() {
        StringBuilder copy;
        if (DiagramType.COMPONENT_DIAGRAM_BASE.equals(diagramType)) {
            // bucket1 is closed by bucket2 automatically
            // bucket2 close with endif
            bucket2.append(EOL.getContent()).append("!endif").append(EOL.getContent());
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
        return "'' !$ENABLE_FEATURE_ORDERING = %true() allows to override the ordering of participant" + EOL.getContent() +
                "'' which is the order of creation by default" + EOL.getContent() +
                "!unquoted function $getOrder($ORDER_PRIO)" + EOL.getContent() +
                "  !if ($ENABLE_FEATURE_ORDERING)" + EOL.getContent() +
                "    !return ' order '+$ORDER_PRIO" + EOL.getContent() +
                "  !else" + EOL.getContent() +
                "    !return ' '" + EOL.getContent() +
                "  !endif" + EOL.getContent() +
                "!endfunction" + EOL.getContent();
    }

    private String getEnableOrderingDefault() {
        return "!if (%not(%variable_exists(\"$ENABLE_FEATURE_ORDERING\")))" + EOL.getContent() +
                "  !$ENABLE_FEATURE_ORDERING = %false()" + EOL.getContent() +
                "!endif" + EOL.getContent();
    }
}
