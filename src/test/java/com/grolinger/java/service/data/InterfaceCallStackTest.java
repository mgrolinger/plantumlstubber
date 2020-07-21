package com.grolinger.java.service.data;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InterfaceCallStackTest {
    private InterfaceCallStack cut;

    @DataProvider
    public Object[][] testBooleanCallStackProvider() {
        return new Object[][]{
                //@formatter:off
                {"",                      false },
                {"/api/interface",                      false },
                {"/api/interface<<DOMAIN>>::GET:POST",  false },
                {"/api/interface<<DOMAIN>>->Call_stack->Call_stack2::GET:POST", true },
                {"/api/interface::GET:POST<<DOMAIN>>->Call_stack->Call_stack2", true },
                {"/api/interface::GET:POST->Call_stack->Call_stack2<<DOMAIN>>", true }
                //@formatter:on
        };
    }

    @Test(dataProvider = "testBooleanCallStackProvider")
    public void testContains(final String originalInterface, final boolean expected) {
        cut = new InterfaceCallStack(originalInterface);
        assertThat(cut.containsCallStack()).isEqualTo(expected);
    }

    @DataProvider
    public Object[][] testInterfaceNameProvider() {
        return new Object[][]{
                //@formatter:off
                {"interface",                                                   "interface" },
                {"/api/interface",                                              "/api/interface" },
                {"/api/interface<<DOMAIN>>::GET:POST",                          "/api/interface" },
                {"/api/interface<<DOMAIN>>->Call_stack->Call_stack2::GET:POST", "/api/interface" },
                {"/api/interface::GET:POST<<DOMAIN>>->Call_stack->Call_stack2", "/api/interface" },
                {"/api/interface::GET:POST->Call_stack->Call_stack2<<DOMAIN>>", "/api/interface" },
                {"/api/interface->Call_stack->Call_stack2::GET:POST<<DOMAIN>>", "/api/interface" },
                {"/api/interface->Call_stack->Call_stack2<<DOMAIN>>::GET:POST", "/api/interface" }
                //@formatter:on
        };
    }

    @Test(dataProvider = "testInterfaceNameProvider")
    public void testInterfaceName(final String originalInterface, final String expectedInterfaceName) {
        cut = new InterfaceCallStack(originalInterface);
        assertThat(cut.getInterfaceName()).isEqualTo(expectedInterfaceName);
    }

    @DataProvider
    public Object[][] testCallStackProvider() {
        String[] methods = new String[]{"Call_stack", "Call_stack2"};
        String[] incl = new String[]{"Call/stack", "Call/stack2"};
        return new Object[][]{
                //@formatter:off
                {"/api/interface",                                                  null, null },
                {"/api/interface<<DOMAIN>>::GET:POST",                              null, null },
                {"/api/interface<<DOMAIN>>->Call_stack->Call_stack2::GET:POST",     methods, incl },
                {"/api/interface::GET:POST<<DOMAIN>>->Call_stack->Call_stack2",     methods, incl },
                {"/api/interface_new::GET:POST->Call_stack->Call_stack2<<DOMAIN>>", methods, incl }
                //@formatter:on
        };
    }

    @Test(dataProvider = "testCallStackProvider")
    public void testCallStack(final String originalInterface, final String[] expectedMethod, final String[] expectedIncludes) {
        cut = new InterfaceCallStack(originalInterface);
        assertThat(cut.getCallStackMethods()).isEqualTo(expectedMethod);
        assertThat(cut.getCallStackIncludes()).isEqualTo(expectedIncludes);
    }
}