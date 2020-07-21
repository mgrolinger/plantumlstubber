package com.grolinger.java.service.data;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MethodDefinitionTest {
    @DataProvider
    public Object[][] testDomainColorProvider() {

        return new Object[][]{
            //@formatter:off
            {null,                              Collections.emptyList() },
            {"",                                Collections.emptyList() },
            {"/api/interface",                  Collections.emptyList() },
            {"/api/interface<<DOMAIN>>",        Collections.emptyList() },
            // wrong MethodSeparator : instead of ::
            {"/api/interface:GET",              Collections.emptyList() },
            // definition only with Method
            {"/api/interface<<DOMAIN>>::GET",   Collections.singletonList(HttpMethod.GET)},
            // full definition
            {"/api/interface<<DOMAIN>>::GET:POST->Call_stack->Call_stack2", Arrays.asList(HttpMethod.GET,HttpMethod.POST) },
            {"/api/interface::GET:POST<<DOMAIN>>->Call_stack->Call_stack2", Arrays.asList(HttpMethod.GET,HttpMethod.POST)},
            {"/api/interface<<DOMAIN>>->Call_stack->Call_stack2::GET:POST:PATCH:DELETE", Arrays.asList(HttpMethod.GET,
                                                                                        HttpMethod.POST,HttpMethod.PATCH,
                                                                                        HttpMethod.DELETE) }
            //@formatter:on
        };
    }

    @Test(dataProvider = "testDomainColorProvider")
    public void testGetDomainColor(final String originalInterfaceName, final List<String> expResult) {
        MethodDefinition methodDefinition = new MethodDefinition(originalInterfaceName);
        assertThat(methodDefinition.getMethods()).isEqualTo(expResult);
    }
}
