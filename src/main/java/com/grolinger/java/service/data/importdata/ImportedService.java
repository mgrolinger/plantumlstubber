package com.grolinger.java.service.data.importdata;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Class that is used in the context of {@link com.grolinger.java.controller.SingleExportController}
 */
@Getter
@Setter
@Builder
public class ImportedService {
    private String application;
    private String customAlias;
    @Builder.Default
    private String systemType = "application";
    private String integrationType;
    private String domainColor;
    private Integer orderPrio;
    private String linkToAlias;
    private String serviceName;
    private String interfaceName;
}
