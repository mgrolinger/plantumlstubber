package com.grolinger.java.config;

public class Service {
    private String application;
    private String integrationType;
    private String colorName;
    private Integer orderPrio;
    private String serviceName;
    private String interfaceName;

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

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public Integer getOrderPrio() {
        return orderPrio;
    }

    public void setOrderPrio(Integer orderPrio) {
        this.orderPrio = orderPrio;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
}
