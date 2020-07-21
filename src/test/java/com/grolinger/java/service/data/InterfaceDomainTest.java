package com.grolinger.java.service.data;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class InterfaceDomainTest {

    @DataProvider
    public Object[][] testDomainColorProvider() {

        return new Object[][]{
                //@formatter:off
                {null,          null,  "default" },
                {null,          "app", "app" },
                {"",            null,  "default"},
                {"",            "app", "app" },
                {"domain",      "app", "app"},
                {"<<doMaIn>>",  "app", "domain" },
                {"<<DOMAIN>>",  "app", "domain" },
                // definition with domainColor
                {"/api/interface<<DOMAIN>>",  "app", "domain" },
                // definition only with Method
                {"/api/interface<<DOMAIN>>::GET",  "app", "domain" },
                // full definition
                {"/api/interface<<DOMAIN>>::GET:POST->Call_stack->Call_stack2",  "app", "domain" },
                {"/api/interface::GET:POST<<DOMAIN>>->Call_stack->Call_stack2",  "app", "domain" },
                {"/api/interface::GET:POST->Call_stack->Call_stack2<<DOMAIN>>",  "app", "domain" }
                //@formatter:on
        };
    }

    @Test(dataProvider = "testDomainColorProvider")
    public void testGetDomainColor(final String originalInterfaceName, final String color, final String expResult) {
        assertThat(InterfaceDomain.extractDomainColor(originalInterfaceName, color)).isEqualTo(expResult);
    }
}