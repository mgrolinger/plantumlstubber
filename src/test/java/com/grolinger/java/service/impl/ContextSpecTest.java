package com.grolinger.java.service.impl;

import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.HttpMethod;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import org.testng.annotations.Test;
import org.thymeleaf.context.Context;

import java.util.Arrays;

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
                //.withCommonPath("commonPath")
                .withOrderPrio(sequenceOrderPrio)
                .getContext();

        assertThat(result.getVariable(APPLICATION_NAME)).isEqualTo("MinApp");
        assertThat(result.getVariable(APPLICATION_LABEL)).isEqualTo("MinLabel");
        assertThat(result.getVariable(ALIAS)).isEqualTo("customAlias");
        assertThat(result.getVariable(COLOR_TYPE)).isEqualTo("<<test>>");
        assertThat(result.getVariable(COLOR_NAME)).isEqualTo("TEST_DOMAIN_COLOR");
        assertThat(result.getVariable(CONNECTION_COLOR)).isEqualTo("TEST_DOMAIN_COLOR_CONNECTION");
        assertThat(result.getVariable(SEQUENCE_PARTICIPANT_ORDER)).isEqualTo(sequenceOrderPrio);
        // Default is ""
        assertThat(result.getVariable(PATH_TO_COMMON_FILE)).isEqualTo("../");
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
        // Default
        assertThat(result.getVariable(PATH_TO_COMMON_FILE)).isEqualTo("");
        assertThat(result.getVariable(IS_ROOT_SERVICE)).isEqualTo(true);
        // these where never set
        assertThat(result.getVariable(SEQUENCE_PARTICIPANT_ORDER)).isNull();
        assertThat(result.getVariable(IS_LINKED)).isNull();
        assertThat(result.getVariable(IS_REST_SERVICE)).isNull();
        assertThat(result.getVariable(IS_SOAP_SERVICE)).isNull();
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
                .withColorName("test")
                .withApplication(applicationDefinition)
                .withServiceDefinition(serviceDefinition)
                .withCommonPath(applicationDefinition.getPathToRoot() + serviceDefinition.getPathToRoot())
                .getContext();

        assertThat(result.getVariable(APPLICATION_NAME)).isEqualTo(name);
        assertThat(result.getVariable(APPLICATION_LABEL)).isEqualTo(label);
        assertThat(result.getVariable(SERVICE_NAME)).isEqualTo("serviceName_v2");
        assertThat(result.getVariable(SERVICE_LABEL)).isEqualTo(sname);
        assertThat(result.getVariable(COLOR_NAME)).isEqualTo("TEST_DOMAIN_COLOR");
        assertThat(result.getVariable(IS_ROOT_SERVICE)).isEqualTo(false);
        assertThat(result.getVariable(PATH_TO_COMMON_FILE)).isEqualTo("../../../");
    }

    @Test
    public void testInterfaceDefinition() {
        ApplicationDefinition applicationDefinition = ApplicationDefinition.builder().name("MinApp").label("MinLabel").build();
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder()
                .originalInterfaceName("/api/rest/interface::POST:PUT->Call_sub")
                .customAlias("customAlias")
                .integrationType("foo::bar")
                .linkToComponent("linked/Component")
                .linkToCustomAlias("linkedAlias")
                .build();
        Context result = new ContextSpec().builder()
                .withColorName("test")
                .withApplication(applicationDefinition)
                .withInterfaceDefinition(interfaceDefinition)
                .withCommonPath(applicationDefinition.getPathToRoot() + interfaceDefinition.getPathToRoot())
                .getContext();

        assertThat(result.getVariable(CALL_INTERFACE_BY)).isEqualTo("$fooCall");
        assertThat(result.getVariable(INTERFACE_NAME)).isEqualTo("api_rest_interface");
        assertThat(result.getVariable(COMPLETE_INTERFACE_NAME)).isEqualTo("MinAppApirestinterfaceInt");
        assertThat(result.getVariable(INTERFACE_RESPONSE_TYPE)).isEqualTo("bar");
        assertThat(result.getVariable(SERVICE_NAME)).isNull();
        // 2x ../ for the interface, 1x ../ for the application
        assertThat(result.getVariable(PATH_TO_COMMON_FILE)).isEqualTo("../../../");
        // neither soap nor rest because of FOO::BAR
        assertThat(result.getVariable(IS_REST_SERVICE)).isEqualTo(false);
        assertThat(result.getVariable(IS_SOAP_SERVICE)).isEqualTo(false);
        assertThat(result.getVariable(COMPONENT_INTEGRATION_TYPE)).isEqualTo("$INTEGRATION_TYPE(FOO::BAR)");
        assertThat(result.getVariable(HTTP_METHODS)).isEqualTo(Arrays.asList(HttpMethod.POST, HttpMethod.PUT));
        assertThat(result.getVariable(CALL_STACK)).isEqualTo(new String[]{"Call_sub"});
        assertThat(result.getVariable(CALL_STACK_INCLUDES)).isEqualTo(new String[]{"Call/sub"});
        assertThat(result.getVariable(IS_LINKED)).isEqualTo(true);
        assertThat(result.getVariable(LINKED_TO_COMPONENT)).isEqualTo("linked_Component");
        assertThat(result.getVariable(LINK_TO_CUSTOM_ALIAS)).isEqualTo("linkedAlias");
        assertThat(result.getVariable(API_CREATED)).isEqualTo("MINAPP_API_API_REST_INTERFACE_CREATED");
    }

    @Test
    public void testInterfaceDefinitionSoap() {
        ApplicationDefinition applicationDefinition = ApplicationDefinition.builder().name("MinApp").label("MinLabel").build();
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder()
                .originalInterfaceName("soapMethod->Call_sub")
                .customAlias("customAlias")
                .integrationType("SOAP")
                .linkToComponent("linked/Component")
                .linkToCustomAlias("linkedAlias")
                .build();
        Context result = new ContextSpec().builder()
                .withColorName("test")
                .withApplication(applicationDefinition)
                .withInterfaceDefinition(interfaceDefinition)
                .withCommonPath(applicationDefinition.getPathToRoot() + interfaceDefinition.getPathToRoot())
                .getContext();

        assertThat(result.getVariable(CALL_INTERFACE_BY)).isEqualTo("$soapCall");
        assertThat(result.getVariable(INTERFACE_NAME)).isEqualTo("soapMethod");
        assertThat(result.getVariable(COMPLETE_INTERFACE_NAME)).isEqualTo("MinAppSoapMethodInt");
        assertThat(result.getVariable(PATH_TO_COMMON_FILE)).isEqualTo("../");
        assertThat(result.getVariable(SERVICE_NAME)).isNull();
        assertThat(result.getVariable(IS_REST_SERVICE)).isEqualTo(false);
        assertThat(result.getVariable(IS_SOAP_SERVICE)).isEqualTo(true);
        // This should be extended from SOAP -> SOAP::XML
        assertThat(result.getVariable(COMPONENT_INTEGRATION_TYPE)).isEqualTo("$INTEGRATION_TYPE(SOAP::XML)");
        assertThat(result.getVariable(INTERFACE_RESPONSE_TYPE)).isEqualTo("XML");
        assertThat(result.getVariable(HTTP_METHODS)).isEqualTo("");
        assertThat(result.getVariable(CALL_STACK)).isEqualTo(new String[]{"Call_sub"});
        assertThat(result.getVariable(CALL_STACK_INCLUDES)).isEqualTo(new String[]{"Call/sub"});
        assertThat(result.getVariable(IS_LINKED)).isEqualTo(true);
        assertThat(result.getVariable(LINKED_TO_COMPONENT)).isEqualTo("linked_Component");
        assertThat(result.getVariable(LINK_TO_CUSTOM_ALIAS)).isEqualTo("linkedAlias");
        assertThat(result.getVariable(API_CREATED)).isEqualTo("MINAPP_API_SOAPMETHOD_CREATED");
    }

    @Test
    public void testServiceDefinitionInterfaceDefinition() {
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

        final String linkToComponent = "component/withSlash";
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder()
                .originalInterfaceName("method.submethod::NotMappable")
                .integrationType("SOAP")
                .linkToComponent(linkToComponent)
                .build();

        Context result = new ContextSpec().builder()
                .withColorName("default")
                .withApplication(applicationDefinition)
                .withServiceDefinition(serviceDefinition)
                .withInterfaceDefinition(interfaceDefinition)
                .withCommonPath(applicationDefinition.getPathToRoot() + serviceDefinition.getPathToRoot() + interfaceDefinition.getPathToRoot())
                .getContext();

        assertThat(result.getVariable(APPLICATION_NAME)).isEqualTo(name);
        assertThat(result.getVariable(APPLICATION_LABEL)).isEqualTo(label);
        assertThat(result.getVariable(SERVICE_NAME)).isEqualTo("serviceName_v2");
        assertThat(result.getVariable(SERVICE_LABEL)).isEqualTo(sname);
        assertThat(result.getVariable(COLOR_NAME)).isEqualTo("DEFAULT_DOMAIN_COLOR");
        assertThat(result.getVariable(IS_ROOT_SERVICE)).isEqualTo(false);
        assertThat(result.getVariable(PATH_TO_COMMON_FILE)).isEqualTo("../../../../");
        assertThat(result.getVariable(LINKED_TO_COMPONENT)).isEqualTo(linkToComponent.replace("/","_"));

        // There is no result for the method "NotMappable" from from the interface
        assertThat(result.getVariable(HTTP_METHODS)).isEqualTo("");
    }
}