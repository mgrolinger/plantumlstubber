package com.grolinger.java.controller;

import com.grolinger.java.service.DecisionService;
import com.grolinger.java.service.NameService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class SingleExportControllerTest {
    @Mock
    private NameService nameService;
    @Mock
    private DecisionService decisionService;

    private SingleExportController cut;
    private Model model = new ConcurrentModel();

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cut = new SingleExportController(nameService,decisionService);
    }

    @Test
    public void testPrepareModel() {
        final String applicationName = "testApplication";
        final String serviceName = "testServiceName/withSeparator";
        final String serviceNameAfterConversion = "TestServiceNameWithSeparator";
        final String integrationType = "TestIntegration";
        final String interfaceName = "testInterfaceName";

        when(decisionService.isCurrentServiceARestService(anyString())).thenReturn(false);
        when(nameService.formatServiceName(anyString(), eq(false))).thenReturn(serviceNameAfterConversion);

        cut.prepareModel(model, applicationName, serviceName, interfaceName, DomainColorMapper.EXTERNAL_DOMAIN_COLOR, integrationType, 1);
        Map<String, Object> modelMap = model.asMap();
        assertThat(modelMap.get(ContextVariables.APPLICATION_NAME.getName())).isEqualTo(StringUtils.capitalize(applicationName));
        assertThat(modelMap.get(ContextVariables.SERVICE_NAME.getName())).isEqualTo(serviceNameAfterConversion);
        assertThat(modelMap.get(ContextVariables.INTERFACE_NAME.getName())).isEqualTo(interfaceName);
        assertThat(modelMap.get(ContextVariables.IS_ROOT_SERVICE.getName())).isEqualTo(false);
        assertThat(modelMap.get(ContextVariables.IS_REST_SERVICE.getName())).isEqualTo(false);
        assertThat(modelMap.get(ContextVariables.COLOR_NAME.getName())).isEqualTo(DomainColorMapper.EXTERNAL_DOMAIN_COLOR.getValue());
        assertThat(modelMap.get(ContextVariables.COLOR_TYPE.getName())).isEqualTo(DomainColorMapper.EXTERNAL_DOMAIN_COLOR.getStereotype());
        assertThat(modelMap.get(ContextVariables.PATH_TO_COMMON_FILE.getName())).isEqualTo("../");
        assertThat(modelMap.get(ContextVariables.DATE_CREATED.getName())).isEqualTo(LocalDate.now());
        // no need to check all here
    }
}