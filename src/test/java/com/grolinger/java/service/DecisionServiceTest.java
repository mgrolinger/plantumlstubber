package com.grolinger.java.service;

import com.grolinger.java.config.Services;
import com.grolinger.java.service.impl.DecisionServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test
@ContextConfiguration(locations = {"classpath:spring-test-config.xml"})
public class DecisionServiceTest {
    @InjectMocks
    private DecisionService cut = new DecisionServiceImpl();

    @BeforeTest
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] isCurrentServiceARestServiceDataProvider() {
        Services servicesRest = new Services();
        servicesRest.setIntegrationType("rest");
        Services servicesRest2 = new Services();
        servicesRest2.setIntegrationType("REST::JSON");
        Services servicesSoap = new Services();
        servicesSoap.setIntegrationType("SOAP::XML");

        return new Object[][]{
                {servicesRest, true},
                {servicesRest2, true},
                {servicesSoap, false},
                {null, false}
        };
    }

    @Test(dataProvider = "isCurrentServiceARestServiceDataProvider")
    public void testIsCurrentServiceARestService(final Services services, final boolean expectedValue) {
        boolean result = cut.isCurrentServiceARestService(services);
        assertThat(result).isEqualTo(expectedValue);
    }

    @DataProvider
    public Object[][] isCurrentServiceARestStringServiceDataProvider() {
        return new Object[][]{
                {"rest", true},
                {"REST:JSON", true},
                {"SOAP", false}
        };
    }

    @Test(dataProvider = "isCurrentServiceARestStringServiceDataProvider")
    public void testIsCurrentServiceARestService(final String integrationType, final Boolean expectedValue) {
        boolean result = cut.isCurrentServiceARestService(integrationType);
        assertThat(result).isEqualTo(expectedValue);
    }

}