package com.grolinger.java.service.impl;

import com.grolinger.java.controller.templatemodel.ContextVariables;
import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.adapter.exportdata.LocalExportAdapter;
import com.grolinger.java.service.adapter.exportdata.LocalStaticAdapter;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.exportdata.ExampleFile;
import com.grolinger.java.service.data.mapper.ColorMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DataProcessorServiceImplTest {
    private SpringTemplateEngine springTemplateEngine = Mockito.mock(SpringTemplateEngine.class);
    @Mock
    private LocalExportAdapter localExportAdapter;
    @Mock
    private LocalStaticAdapter localStaticAdapter;
    @Mock
    private Logger log;
    @InjectMocks
    DataProcessorServiceImpl dataProcessorServiceImpl;
    private AutoCloseable closeable;

    @BeforeMethod
    public void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterMethod
    public void releaseMocks() throws Exception {
        closeable.close();
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
        when(localExportAdapter.createDirectory(anyString(), anyString(), any(ApplicationDefinition.class))).thenReturn("createDirectoryResponse");
        when(localExportAdapter.createServiceDirectory(anyString(), any(), any())).thenReturn("createServiceDirectoryResponse");
        when(localExportAdapter.writeInterfaceFile(anyString(), any(), any(), any(), any(), any())).thenReturn(new ExampleFile(DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE));


        dataProcessorServiceImpl.processApplication(Collections.singletonList(applicationDefinition), DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE);

        //then
        verify(localExportAdapter).createDirectory(
                eq(DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE.getBasePath()),
                eq(""),
                any(ApplicationDefinition.class));
        // 1 service -> 1 call
        verify(localExportAdapter).createServiceDirectory(
                eq(DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE.getBasePath()),
                any(ApplicationDefinition.class),
                any(ServiceDefinition.class));
        // 2 interfaces -> 2 calls
        verify(localExportAdapter, times(2)).writeInterfaceFile(
                eq("createServiceDirectoryResponse"),
                any(ApplicationDefinition.class),
                any(ServiceDefinition.class),
                any(InterfaceDefinition.class),
                any(Context.class),
                any(ExampleFile.class));
    }
}
