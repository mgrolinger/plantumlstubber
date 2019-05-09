package com.grolinger.java.controller;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ConstantsTest {

    @Test
    public void getValue() {
        assertThat(Constants.EMPTY.getValue()).isEqualTo("EMPTY");
        assertThat(Constants.DEFAULT_ROOT_SERVICE_NAME.getValue()).isEqualTo("");
    }

    @Test
    public void getFirstChar() {
        assertThat(Constants.EMPTY.getFirstChar()).isEqualTo('E');
        assertThat(Constants.DEFAULT_ROOT_SERVICE_NAME.getFirstChar()).isEqualTo(Character.MIN_VALUE);
    }
}