package com.grolinger.java.service.impl;

import com.grolinger.java.controller.templatemodel.ContextVariables;
import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.controller.templatemodel.Template;
import com.grolinger.java.controller.templatemodel.TemplateContent;
import com.grolinger.java.service.adapter.FileService;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.exportdata.ExampleFile;
import com.grolinger.java.service.data.mapper.ColorMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DataProcessorServiceImplTest {
    @Mock
    FileService fileService;
    @Mock
    Logger log;
    @InjectMocks
    DataProcessorServiceImpl dataProcessorServiceImpl;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessContextOfApplication() {
        final String colorName = "colorName";
        final String applicationName = "applicationName";
        final String serviceName = "serviceName";
        final String interfaceName = "interfaceName";
        Context result = dataProcessorServiceImpl.processContextOfApplication(colorName, "integrationType", applicationName, serviceName, interfaceName, Integer.valueOf(12));
        assertThat(result).isNotNull();
        assertThat(result.getVariable(ContextVariables.COLOR_NAME)).isEqualTo(ColorMapper.getDomainColor(colorName));
        assertThat(result.getVariable(ContextVariables.APPLICATION_NAME)).isEqualTo(StringUtils.capitalize(applicationName));
        assertThat(result.getVariable(ContextVariables.SERVICE_NAME)).isEqualTo(serviceName);
        assertThat(result.getVariable(ContextVariables.INTERFACE_NAME)).isEqualTo(interfaceName);
        assertThat(result.getVariable(ContextVariables.SEQUENCE_PARTICIPANT_ORDER)).isEqualTo(12);
    }

    @Test
    public void testProcessApplication() throws IOException {
        // given
        final InterfaceDefinition interfaceDefinition1 = InterfaceDefinition.builder()
                .originalInterfaceName("/api/interface")
                .integrationType("REST")
                .customAlias("cAlias")
                .build();
        final InterfaceDefinition interfaceDefinition2 = InterfaceDefinition.builder()
                .originalInterfaceName("/api/interface2")
                .integrationType("REST")
                .customAlias("cAlias")
                .build();
        final ServiceDefinition serviceDefinition = ServiceDefinition.builder()
                .serviceName("sName")
                .serviceLabel("sLabel")
                .domainColor("dColor")
                .interfaceDefinitions(Arrays.asList(interfaceDefinition1, interfaceDefinition2))
                .build();
        final String appName = UUID.randomUUID().toString();
        final ApplicationDefinition applicationDefinition = ApplicationDefinition.builder()
                .name(appName)
                .systemType("application")
                .orderPrio(14)
                .label("aLabel")
                .serviceDefinitions(Collections.singletonList(serviceDefinition))
                .build();

        // when
        // although only two interfaces, create the directory only once
        when(fileService.createDirectory(anyString(), anyString(), anyString())).thenReturn("createDirectoryResponse");
        when(fileService.createServiceDirectory(anyString(), any(), any())).thenReturn("createServiceDirectoryResponse");
        when(fileService.writeInterfaceFile(anyString(), any(), any(), any(), any(), any())).thenReturn(new ExampleFile(Template.COMPONENT_V1_2020_7, TemplateContent.START));


        dataProcessorServiceImpl.processApplication(Collections.singletonList(applicationDefinition), DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE);

        //then
        verify(fileService).createDirectory(
                eq(DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE.getBasePath()),
                eq(""),
                eq(appName));
        // 1 service -> 1 call
        verify(fileService).createServiceDirectory(
                eq(DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE.getBasePath()),
                any(ApplicationDefinition.class),
                any(ServiceDefinition.class));
        // 2 interfaces -> 2 calls
        verify(fileService, times(2)).writeInterfaceFile(
                eq("createServiceDirectoryResponse"),
                any(ApplicationDefinition.class),
                any(ServiceDefinition.class),
                any(InterfaceDefinition.class),
                any(Context.class),
                any(ExampleFile.class));
    }
}
