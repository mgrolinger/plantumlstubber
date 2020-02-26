package com.grolinger.java.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ConfigurationProperties(prefix = "impex")
public class PlantumlStubberConfiguration {
    @NotBlank
    private String defaultPath;
}