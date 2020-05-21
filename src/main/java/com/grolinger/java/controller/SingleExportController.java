package com.grolinger.java.controller;

import com.grolinger.java.controller.templatemodel.Template;
import com.grolinger.java.service.DataProcessorService;
import com.grolinger.java.service.data.importdata.ImportedService;
import com.grolinger.java.service.data.mapper.ColorMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static com.grolinger.java.controller.templatemodel.ContextVariables.*;

@Deprecated(since = "31.12.2019")
@Controller
public class SingleExportController {
    private final DataProcessorService dataProcessorService;


    @Autowired
    SingleExportController(DataProcessorService dataProcessorService) {
        this.dataProcessorService = dataProcessorService;

    }

    @ApiOperation(
            value = "Exports a single component stub."
    )
    @GetMapping(path = "/component/{applicationName}/service/{serviceName}/interface/{interfaceName}/color/{colorName}/{integrationType}", produces = "text/html")
    public String singleComponent(Model model, @PathVariable final String applicationName, @PathVariable final String serviceName, @PathVariable final String interfaceName, @ApiParam(allowableValues = "customer,financial,integration,resource,external") @PathVariable final String colorName, @PathVariable final String integrationType, @ApiParam(allowableValues = "1,2") @RequestParam(required = false, defaultValue = "2") String preprocessorVersion) {
        prepareModel(model, applicationName, serviceName, interfaceName, ColorMapper.getDomainColor(colorName), integrationType, null);
        return Template.COMPONENT_V1_2019_6.getTemplateURL();
    }

    @ApiOperation(
            value = "Exports a single component stub."
    )
    @PostMapping(path = "/component", produces = "text/html", consumes = "application/json")
    public String singleComponent(Model model, @NotNull @RequestBody ImportedService importedService, @RequestParam(required = false, defaultValue = "2") String preprocessorVersion) {
        prepareModel(model, importedService.getApplication(), importedService.getServiceName(), importedService.getInterfaceName(), ColorMapper.getDomainColor(importedService.getDomainColor()), importedService.getIntegrationType(), null);
        return Template.COMPONENT_V1_2019_6.getTemplateURL();
    }

    @ApiOperation(
            value = "Exports a single sequence stub."
    )
    @GetMapping(path = "/sequence/{applicationName}/service/{serviceName}/interface/{interfaceName}/color/{colorName}/{integrationType}/order/{orderPrio}")
    public String singleSequence(Model model, @PathVariable final String applicationName, @PathVariable final String serviceName, @PathVariable final String interfaceName, @ApiParam(allowableValues = "customer,financial,integration,resource,external") @PathVariable final String colorName, @PathVariable final String integrationType, @PathVariable final int orderPrio, @ApiParam(allowableValues = "1,2") @RequestParam(required = false, defaultValue = "2") String preprocessorVersion) {
        prepareModel(model, applicationName, serviceName, interfaceName, ColorMapper.getDomainColor(colorName), integrationType, orderPrio);
        return Template.SEQUENCE_V1_2019_6.getTemplateURL();
    }

    @ApiOperation(
            value = "Exports a single sequence stub."
    )
    @PostMapping(path = "/sequence", produces = "text/html", consumes = "application/json")
    public String singleSequence(Model model, @NotNull @RequestBody ImportedService importedService, @RequestParam(required = false, defaultValue = "2") String preprocessorVersion) {
        prepareModel(model, importedService.getApplication(), importedService.getServiceName(), importedService.getInterfaceName(), ColorMapper.getDomainColor(importedService.getDomainColor()), importedService.getIntegrationType(), null);
        return Template.SEQUENCE_V1_2019_6.getTemplateURL();
    }

    /**
     * Prepares the model to export
     *
     * @param model
     * @param applicationName
     * @param serviceName
     * @param interfaceName
     * @param colorName
     * @param integrationType
     * @param orderPrio
     */
    void prepareModel(Model model, @PathVariable String applicationName, @PathVariable String serviceName, @PathVariable String interfaceName, @PathVariable String colorName, @PathVariable String integrationType, @PathVariable Integer orderPrio) {
        model.addAttribute(DATE_CREATED, LocalDate.now());
        //TODO Call Stack
        // TODO fix all
        Context context = dataProcessorService.processContextOfApplication(colorName, integrationType, applicationName, serviceName, interfaceName, orderPrio);

        model.addAttribute(PATH_TO_COMMON_FILE, context.getVariable(PATH_TO_COMMON_FILE));
        model.addAttribute(APPLICATION_NAME, context.getVariable(APPLICATION_NAME));
        model.addAttribute(ALIAS, context.getVariable(ALIAS));
        model.addAttribute(SERVICE_NAME, context.getVariable(SERVICE_NAME));
        model.addAttribute(INTERFACE_NAME, context.getVariable(INTERFACE_NAME));
        model.addAttribute(COLOR_TYPE, context.getVariable(COLOR_TYPE));
        model.addAttribute(CONNECTION_COLOR, context.getVariable(CONNECTION_COLOR));
        model.addAttribute(COLOR_NAME, context.getVariable(COLOR_NAME));
        if (orderPrio != null) {
            model.addAttribute(SEQUENCE_PARTICIPANT_ORDER, orderPrio);
        }
        model.addAttribute(IS_ROOT_SERVICE, context.getVariable(IS_ROOT_SERVICE));
        model.addAttribute(IS_SOAP_SERVICE, context.getVariable(IS_SOAP_SERVICE));
        model.addAttribute(COMPONENT_INTEGRATION_TYPE, context.getVariable(COMPONENT_INTEGRATION_TYPE));
        model.addAttribute(COMPLETE_INTERFACE_NAME, context.getVariable(COMPLETE_INTERFACE_NAME));
        model.addAttribute(API_CREATED, context.getVariable(API_CREATED));


    }
}