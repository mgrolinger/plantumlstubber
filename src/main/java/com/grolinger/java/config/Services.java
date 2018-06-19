package com.grolinger.java.config;

import java.util.Map;

public class Services {
    private String application;
    private String integrationType;
    private String  color;
    private Map<String,String[]> services;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(String integrationType) {
        this.integrationType = integrationType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Map<String, String[]> getServices() {
        return services;
    }

    public void setServices(Map<String, String[]> services) {
        this.services = services;
    }
}
