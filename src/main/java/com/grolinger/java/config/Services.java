package com.grolinger.java.config;

import java.util.Map;

public class Services {
    private String application;
    private String integrationType;
    private String color;
    private String orderPrio="0";
    private Map<String, String[]> serviceList;

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

    public String getOrderPrio() {
        return orderPrio;
    }

    public void setOrderPrio(String orderPrio) {
        this.orderPrio = orderPrio;
    }

    public Map<String, String[]> getServices() {
        return serviceList;
    }

    public void setServices(Map<String, String[]> services) {
        this.serviceList = services;
    }
}
