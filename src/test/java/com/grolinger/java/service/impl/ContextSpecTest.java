package com.grolinger.java.service.impl;

import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import org.testng.annotations.Test;
import org.thymeleaf.context.Context;

import static com.grolinger.java.controller.templatemodel.ContextVariables.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ContextSpecTest {

    @Test
    public void testMinObject() {
        ApplicationDefinition applicationDefinition = ApplicationDefinition.builder()
                .name("MinApp")
                .label("MinLabel")
                .alias("customAlias")
                .systemType("application")
                .build();
        final int sequenceOrderPrio = 99;
        Context result = new ContextSpec().builder()
                .withColorName("test")
                .withApplication(applicationDefinition)
                .withCommonPath("commonPath")
                .withOrderPrio(sequenceOrderPrio)
                .getContext();

        assertThat(result.getVariable(APPLICATION_NAME)).isEqualTo("MinApp");
        assertThat(result.getVariable(APPLICATION_LABEL)).isEqualTo("MinLabel");
        assertThat(result.getVariable(ALIAS)).isEqualTo("customAlias");
        assertThat(result.getVariable(COLOR_TYPE)).isEqualTo("<<test>>");
        assertThat(result.getVariable(COLOR_NAME)).isEqualTo("TEST_DOMAIN_COLOR");
        assertThat(result.getVariable(CONNECTION_COLOR)).isEqualTo("TEST_DOMAIN_COLOR_CONNECTION");
        assertThat(result.getVariable(SEQUENCE_PARTICIPANT_ORDER)).isEqualTo(sequenceOrderPrio);
        assertThat(result.getVariable(PATH_TO_COMMON_FILE)).isEqualTo("commonPath");
    }

    @Test
    public void testNull() {
        Context result = new ContextSpec().builder()
                .withColorName(null)
                .withApplication(ApplicationDefinition.builder().build()).getContext();

        assertThat(result.getVariable(APPLICATION_NAME)).isEqualTo("Undefined");
        assertThat(result.getVariable(APPLICATION_LABEL)).isEqualTo("Undefined");
        assertThat(result.getVariable(ALIAS)).isEqualTo("undefined");
        assertThat(result.getVariable(COLOR_TYPE)).isEqualTo("<<undefined>>");
        assertThat(result.getVariable(COLOR_NAME)).isEqualTo("UNDEFINED_DOMAIN_COLOR");
        assertThat(result.getVariable(CONNECTION_COLOR)).isEqualTo("UNDEFINED_DOMAIN_COLOR_CONNECTION");
        assertThat(result.getVariable(PATH_TO_COMMON_FILE)).isEqualTo("");
        assertThat(result.getVariable(IS_ROOT_SERVICE)).isEqualTo(true);
        // these where never set
        assertThat(result.getVariable(SEQUENCE_PARTICIPANT_ORDER)).isNull();
        assertThat(result.getVariable(IS_LINKED)).isNull();
        assertThat(result.getVariable(IS_REST_SERVICE)).isNull();
        assertThat(result.getVariable(IS_SOAP_SERVICE)).isNull();
    }

    @Test
    public void testInterfaceDefinition() {
        ApplicationDefinition applicationDefinition = ApplicationDefinition.builder().name("MinApp").label("MinLabel").build();
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder()
                .originalInterfaceName("/api/rest/interface::POST:PUT->Call_sub")
                .customAlias("customAlias")
                .integrationType("foo::bar")
                .linkToComponent("linkedComponent")
                .linkToCustomAlias("linkedAlias")
                .build();
        Context result = new ContextSpec().builder()
                .withColorName("test")
                .withApplication(applicationDefinition)
                .withInterfaceDefinition(interfaceDefinition)
                .getContext();

        assertThat(result.getVariable(CALL_INTERFACE_BY)).isEqualTo("$fooCall");
        assertThat(result.getVariable(INTERFACE_NAME)).isEqualTo("_api_rest_interface");
        assertThat(result.getVariable(COMPLETE_INTERFACE_NAME)).isEqualTo("MinAppApirestinterfaceInt");
        assertThat(result.getVariable(INTERFACE_RESPONSE_TYPE)).isEqualTo("bar");
        assertThat(result.getVariable(SERVICE_NAME)).isNull();
        // neither soap nor rest because of FOO::BAR
        assertThat(result.getVariable(IS_REST_SERVICE)).isEqualTo(false);
        assertThat(result.getVariable(IS_SOAP_SERVICE)).isEqualTo(false);
        assertThat(result.getVariable(COMPONENT_INTEGRATION_TYPE)).isEqualTo("$INTEGRATION_TYPE(foo::bar)");
        assertThat(result.getVariable(HTTP_METHODS)).isEqualTo("$INDIVIDUAL_METHODS('[POST, PUT]')");
        assertThat(result.getVariable(CALL_STACK)).isEqualTo(new String[]{"Call_sub"});
        assertThat(result.getVariable(CALL_STACK_INCLUDES)).isEqualTo(new String[]{"Call/sub"});
        assertThat(result.getVariable(IS_LINKED)).isEqualTo(true);
        assertThat(result.getVariable(LINKED_TO_COMPONENT)).isEqualTo("linkedComponent");
        assertThat(result.getVariable(LINK_TO_CUSTOM_ALIAS)).isEqualTo("linkedAlias");
        assertThat(result.getVariable(API_CREATED)).isEqualTo("MINAPP_API__API_REST_INTERFACE_CREATED");
    }

    @Test
    public void testServiceDefinition() {
        final String name = "MinApp";
        final String label = "MinLabel";
        ApplicationDefinition applicationDefinition = ApplicationDefinition.builder()
                .name(name)
                .label(label)
                .orderPrio(98)
                .build();

        final String sname = "serviceName.v2";
        ServiceDefinition serviceDefinition = ServiceDefinition.builder()
                .serviceName(sname)

                .build();
        Context result = new ContextSpec().builder()
                .withColorName("test").withApplication(applicationDefinition).withServiceDefinition(serviceDefinition).getContext();

        assertThat(result.getVariable(APPLICATION_NAME)).isEqualTo(name);
        assertThat(result.getVariable(APPLICATION_LABEL)).isEqualTo(label);
        assertThat(result.getVariable(SERVICE_NAME)).isEqualTo("serviceName_v2");
        assertThat(result.getVariable(SERVICE_LABEL)).isEqualTo(sname);
        assertThat(result.getVariable(COLOR_NAME)).isEqualTo("TEST_DOMAIN_COLOR");
        assertThat(result.getVariable(IS_ROOT_SERVICE)).isEqualTo(false);
        assertThat(result.getVariable(PATH_TO_COMMON_FILE)).isEqualTo("../../");
    }
}