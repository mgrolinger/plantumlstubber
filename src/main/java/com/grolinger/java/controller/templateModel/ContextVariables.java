package com.grolinger.java.controller.templateModel;

public enum ContextVariables {
    COLOR_TYPE("colorType"),
    COLOR_NAME("colorName"),
    CONNECTION_COLOR("connectionColor"),
    COMPONENT_INTEGRATION_TYPE("componentIntegrationType"),
    COMPLETE_INTERFACE_NAME("COMPLETE_INTERFACE_PATH"),
    DATE_CREATED("dateCreated"),
    SEQUENCE_PARTICIPANT_ORDER("sequenceOrderPrio"),
    API_CREATED("API_CREATED"),
    PATH_TO_COMMON_FILE("commonPath"),
    IS_ROOT_SERVICE("isRootService"),
    IS_REST_SERVICE("isRestService"),
    APPLICATION_NAME("applicationName"),
    SERVICE_NAME("serviceName"),
    REST("REST"),
    APPLICATION_NAME_SHORT("applicationNameShort"),
    INTERFACE_NAME("interfaceName");


    private String name;

    ContextVariables(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
