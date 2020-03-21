package com.grolinger.java.controller;

import com.grolinger.java.controller.templatemodel.ContextVariables;
import com.grolinger.java.service.DataProcessorService;
import com.grolinger.java.service.adapter.FileService;
import com.grolinger.java.service.data.mapper.ColorMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.thymeleaf.context.Context;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class SingleExportControllerTest {
    @Mock
    private FileService fileService;
    @Mock
    private DataProcessorService dataProcessorService;
    @InjectMocks
    private SingleExportController cut;
    private Model model = new ConcurrentModel();

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cut = new SingleExportController(dataProcessorService);
    }

    @Test
    public void testPrepareModel() {
        final String applicationName = "testApplication";
        final String serviceName = "testServiceName/withSeparator";
        final String serviceNameAfterConversion = "TestServiceNameWithSeparator";
        final String integrationType = "TestIntegration";
        final String interfaceName = "testInterfaceName";
        final String colorName = "external";
        final String commonPath = "../";
        Context context = new Context();
        context.setVariable(ContextVariables.COLOR_NAME, ColorMapper.getDomainColor(colorName));
        context.setVariable(ContextVariables.APPLICATION_NAME, StringUtils.capitalize(applicationName));
        context.setVariable(ContextVariables.SERVICE_NAME, serviceNameAfterConversion);
        context.setVariable(ContextVariables.INTERFACE_NAME, interfaceName);
        context.setVariable(ContextVariables.COMPONENT_INTEGRATION_TYPE, integrationType);
        context.setVariable(ContextVariables.PATH_TO_COMMON_FILE, commonPath);


        when(dataProcessorService.processContextOfApplication(eq(colorName), anyString(), eq(applicationName), eq(serviceName), eq(interfaceName), eq(1))).thenReturn(context);

        cut.prepareModel(model, applicationName, serviceName, interfaceName, colorName, integrationType, 1);

        Map<String, Object> modelMap = model.asMap();
        // no need to check all here
        assertThat(modelMap.get(ContextVariables.APPLICATION_NAME)).isEqualTo(StringUtils.capitalize(applicationName));
        assertThat(modelMap.get(ContextVariables.SERVICE_NAME)).isEqualTo(serviceNameAfterConversion);
        assertThat(modelMap.get(ContextVariables.INTERFACE_NAME)).isEqualTo(interfaceName);
        assertThat(modelMap.get(ContextVariables.COLOR_NAME)).isEqualTo(ColorMapper.getDomainColor(colorName));
        assertThat(modelMap.get(ContextVariables.PATH_TO_COMMON_FILE)).isEqualTo(commonPath);
    }
}