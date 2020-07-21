package com.grolinger.java.service.data;


import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InterfaceDefinitionTest {


    @DataProvider
    public Object[][] testNameDataProvider() {
        InterfaceDefinition one = InterfaceDefinition.builder()
                .originalInterfaceName("method")
                .integrationType("SoAp")
                .build();
        List<String> resultOne = Lists.newArrayList("method");
        InterfaceDefinition two = InterfaceDefinition.builder()
                .originalInterfaceName("/api/v1/resource/{id}::POST:GET->Call_Some_Application->And_some_other")
                .integrationType("rEsT::XML")
                .build();
        List<String> resultTwo = Lists.newArrayList("api", "v1", "resource", "id");
        InterfaceDefinition three = InterfaceDefinition.builder()
                .originalInterfaceName("/api/v1/resource/{id}/")
                .integrationType("rEsT")
                .build();
        List<String> resultThree = Lists.newArrayList("api", "v1", "resource", "id");
        // Oh well this will later cause an exception while exporting the file,
        // because we won't have a name for the file
        InterfaceDefinition four = InterfaceDefinition.builder()
                .originalInterfaceName("/")
                .integrationType("rEsT")
                .build();
        List<String> resultFour = Collections.emptyList();
        InterfaceDefinition five = InterfaceDefinition.builder()
                .originalInterfaceName("/")
                .integrationType("foo::bar")
                .build();
        List<String> resultFive = Collections.emptyList();
        InterfaceDefinition six = InterfaceDefinition.builder()
                .originalInterfaceName("/withCallStack/{id}/foo->Filesystem_readFile->Filesystem_writeFile<<authentifizierung>>::POST:GET")
                .applicationDomainColor("authentifizierung")
                .integrationType("rest")
                .build();
        InterfaceDefinition sixA = InterfaceDefinition.builder()
                .originalInterfaceName("/withCallStack/{id}/foo::POST:GET->Filesystem_readFile->Filesystem_writeFile<<authentifizierung>>")
                .applicationDomainColor("authentifizierung")
                .integrationType("rest")
                .build();
        List<String> resultSix = Lists.newArrayList("withCallStack", "id", "foo");
        return new Object[][]{
                //@formatter:off
                {one   , "method",                 resultOne,   "method",               "SOAP::XML" , "method", false},
                {two   , "/api/v1/resource/{id}",  resultTwo,   "api_v1_resource_id",   "REST::XML" , "id"  , true},
                // The slash prevents a method name
                {three , "/api/v1/resource/{id}/", resultThree, "api_v1_resource_id",   "REST::JSON",  "" , false },
                {four  , "/",                      resultFour,  ""                  ,   "REST::JSON",  "" , false },
                {five  , "/",                      resultFive,  ""                  ,   "FOO::BAR",    "" , false },
                {six,  "/withCallStack/{id}/foo",  resultSix,   "withCallStack_id_foo", "REST::JSON",  "foo",true },
                {sixA,  "/withCallStack/{id}/foo",  resultSix,   "withCallStack_id_foo", "REST::JSON",  "foo",true }
                //@formatter:on
        };
    }

    @Test(dataProvider = "testNameDataProvider")
    public void testName(final InterfaceDefinition cut,
                         final String expName,
                         final List<String> expParts,
                         final String expCallName,
                         final String expInteg,
                         final String expMethod,
                         final boolean hasCallStack) {
        assertThat(cut.getName()).isEqualTo(expName);
        assertThat(cut.getNameParts()).isEqualTo(expParts);
        assertThat(cut.getCallName()).isEqualTo(expCallName);
        assertThat(cut.getIntegrationType()).isEqualTo(expInteg);
        assertThat(cut.getMethodName()).isEqualTo(expMethod);
        if (hasCallStack) {
            assertThat(cut.getCallStack().getCallStackMethods()).isNotEmpty();
            assertThat(cut.getCallStack().getCallStackIncludes()).isNotEmpty();
        } else {
            assertThat(cut.getCallStack().getCallStackMethods()).isNull();
        }
    }

    @DataProvider
    public Object[][] testContainsPathDataProvider() {
        //This case should not happen
        InterfaceDefinition zero = InterfaceDefinition.builder()
                .originalInterfaceName("")
                .integrationType("SoAp")
                .build();
        InterfaceDefinition one = InterfaceDefinition.builder()
                .originalInterfaceName("method")
                .integrationType("SoAp")
                .build();
        InterfaceDefinition two = InterfaceDefinition.builder()
                .originalInterfaceName("/api/v1/resource/{id}::POST:GET->Call_Some_Application->And_some_other")
                .integrationType("rEsT")
                .build();
        InterfaceDefinition three = InterfaceDefinition.builder()
                .originalInterfaceName("/api/v1/resource/{id}/")
                .integrationType("rEsT")
                .build();
        return new Object[][]{
                //@formatter:off
                {zero,  false,  false},
                {one,   true,   false},
                {two,   true,   false},
                {three, true,   true}
                //@formatter:on
        };
    }

    @Test(dataProvider = "testContainsPathDataProvider")
    public void testContainsPath(final InterfaceDefinition cut, final boolean expContainsPath, final boolean expSlash) {
        assertThat(cut.containsPath()).isEqualTo(expContainsPath);
    }

    @DataProvider
    public Object[][] testContainsCallStackDataProvider() {
        InterfaceDefinition one = InterfaceDefinition.builder()
                .originalInterfaceName("method")
                .integrationType("SoAp")
                .build();
        InterfaceDefinition two = InterfaceDefinition.builder()
                .originalInterfaceName("/api/v1/resource/{id}<<domainColor>>::POST:GET->Call_Some_Application->And_some_other")
                .integrationType("rEsT")
                .build();

        return new Object[][]{
                //@formatter:off
                {one, false, null,                                                      null},
                {two, true,  new String[]{"Call_Some_Application", "And_some_other"},   new String[]{"Call/Some/Application", "And/some/other"}},
                //@formatter:on
        };
    }

    @Test(dataProvider = "testContainsCallStackDataProvider")
    public void testContainsCallStack(final InterfaceDefinition cut,
                                      final boolean containsCallStack,
                                      final String[] callStack,
                                      final String[] includesStack) {
        assertThat(cut.containsCallStack()).isEqualTo(containsCallStack);
        assertThat(cut.getCallStack().getCallStackMethods()).isEqualTo(callStack);
        assertThat(cut.getCallStack().getCallStackIncludes()).isEqualTo(includesStack);
    }

    @Test
    public void testBuilder() {
        InterfaceDefinition.InterfaceDefinitionBuilder result = InterfaceDefinition.builder();
        assertThat(result).isNotNull();
    }

    @DataProvider
    public Object[][] testGetPathToRootDataProvider() {
        InterfaceDefinition one = InterfaceDefinition.builder()
                .originalInterfaceName("/api/v1/resource/{id}::POST:GET->Call_Some_Application")
                .integrationType("rEsT")
                .build();
        InterfaceDefinition two = InterfaceDefinition.builder()
                .originalInterfaceName("method")
                .integrationType("SoAp")
                .build();

        return new Object[][]{
                //@formatter:off
                {one,"../../../"},
                {two ,""}
                //@formatter:on
        };
    }

    @Test(dataProvider = "testGetPathToRootDataProvider")
    public void testGetPathToRoot(final InterfaceDefinition cut, final String expRootPath) {
        assertThat(cut.getPathToRoot()).isEqualTo(expRootPath);

    }

    @DataProvider
    public Object[][] testGetMethodDataProvider() {
        InterfaceDefinition one = InterfaceDefinition.builder()
                .originalInterfaceName("/api/v1/resource/{id}::POST:GET->Call_Some_Application")
                .integrationType("rEsT")
                .build();
        List<HttpMethod> expResultOne = Lists.asList(HttpMethod.POST, new HttpMethod[]{HttpMethod.GET});

        InterfaceDefinition two = InterfaceDefinition.builder()
                .originalInterfaceName("method")
                .integrationType("SoAp")
                .build();
        List<HttpMethod> expResultTwo = Collections.emptyList();

        InterfaceDefinition three = InterfaceDefinition.builder()
                .originalInterfaceName("method::")
                .integrationType("SoAp")
                .build();
        List<HttpMethod> expResultThree = Collections.emptyList();

        InterfaceDefinition four = InterfaceDefinition.builder()
                .originalInterfaceName("method")
                .integrationType("SomethingUnknown")
                .build();

        return new Object[][]{
                //@formatter:off
                {one,   expResultOne},
                {two ,  expResultTwo},
                {three, expResultThree},
                {four, expResultThree}
                //@formatter:on
        };
    }

    @Test(dataProvider = "testGetMethodDataProvider")
    public void testGetMethodDefinition(final InterfaceDefinition cut, final List<HttpMethod> expMethods) {
        assertThat(cut.getMethodDefinition()).isNotNull();
        assertThat(cut.getMethodDefinition().getMethods()).isEqualTo(expMethods);
    }
}