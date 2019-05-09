package com.grolinger.java.controller;

import com.grolinger.java.config.Service;
import com.grolinger.java.service.DecisionService;
import com.grolinger.java.service.NameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.context.Context;

import java.time.LocalDate;

import static com.grolinger.java.controller.ContextVariables.*;

@Controller
public class SingleExportController {
    private final NameService nameService;
    private final DecisionService decisionService;

    @Autowired
    SingleExportController(NameService nameService, DecisionService decisionService) {
        this.nameService = nameService;
        this.decisionService = decisionService;
    }

    @GetMapping("/component/{applicationName}/service/{serviceName}/interface/{interfaceName}/color/{colorName}/{integrationType}")
    public String singleComponent(Model model, @PathVariable final String applicationName, @PathVariable final String serviceName, @PathVariable final String interfaceName, @PathVariable final String colorName, @PathVariable final String integrationType) {
        prepareModel(model, applicationName, serviceName, interfaceName, DomainColorMapper.getByType(colorName), integrationType, null);
        return "componentExport";
    }

    @PostMapping(value = "/component", produces = "text/html", consumes = "application/json")
    public String singleComponent(Model model, @org.jetbrains.annotations.NotNull @RequestBody Service service) {
        prepareModel(model, service.getApplication(), service.getServiceName(), service.getInterfaceName(), DomainColorMapper.getByType(service.getColorName()), service.getIntegrationType(), null);
        return "componentExport";
    }


    @GetMapping("/sequence/{applicationName}/service/{serviceName}/interface/{interfaceName}/color/{colorName}/{integrationType}/order/{orderPrio}")
    public String singleSequence(Model model, @PathVariable final String applicationName, @PathVariable final String serviceName, @PathVariable final String interfaceName, @PathVariable final String colorName, @PathVariable final String integrationType, @PathVariable final int orderPrio) {
        prepareModel(model, applicationName, serviceName, interfaceName, DomainColorMapper.getByType(colorName), integrationType, orderPrio);
        return "sequenceExport";
    }

    @PostMapping(value = "/sequence", produces = "text/html", consumes = "application/json")
    public String singleSequence(Model model, @org.jetbrains.annotations.NotNull @RequestBody Service service) {
        prepareModel(model, service.getApplication(), service.getServiceName(), service.getInterfaceName(), DomainColorMapper.getByType(service.getColorName()), service.getIntegrationType(), null);
        return "sequenceExport";
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
    void prepareModel(Model model, @PathVariable String applicationName, @PathVariable String serviceName, @PathVariable String interfaceName, @PathVariable DomainColorMapper colorName, @PathVariable String integrationType, @PathVariable Integer orderPrio) {
        model.addAttribute(DATE_CREATED.getName(), LocalDate.now());
        Context context = new ContextSpec().builder()
                .withColorName(colorName)
                .withIntegrationType(integrationType)
                .withApplicationName(applicationName)
                .withPreformattedServiceName(nameService.formatServiceName(serviceName, decisionService.isCurrentServiceARestService(integrationType)))
                //FIXME move those service calls to the Builder
                .withInterfaceName(nameService.replaceUnwantedCharacters(interfaceName,false))
                .withOrderPrio(orderPrio)
                .withCommonPath("../")
                .getContext();
        model.addAttribute(PATH_TO_COMMON_FILE.getName(), context.getVariable(PATH_TO_COMMON_FILE.getName()));
        model.addAttribute(APPLICATION_NAME.getName(), context.getVariable(APPLICATION_NAME.getName()));
        model.addAttribute(APPLICATION_NAME_SHORT.getName(), context.getVariable(APPLICATION_NAME_SHORT.getName()));
        model.addAttribute(SERVICE_NAME.getName(), context.getVariable(SERVICE_NAME.getName()));
        model.addAttribute(INTERFACE_NAME.getName(), context.getVariable(INTERFACE_NAME.getName()));
        model.addAttribute(COLOR_TYPE.getName(), context.getVariable(COLOR_TYPE.getName()));
        model.addAttribute(CONNECTION_COLOR.getName(), context.getVariable(CONNECTION_COLOR.getName()));
        model.addAttribute(COLOR_NAME.getName(), context.getVariable(COLOR_NAME.getName()));
        if (orderPrio != null) {
            model.addAttribute(SEQUENCE_PARTICIPANT_ORDER.getName(), orderPrio);
        }
        model.addAttribute(IS_ROOT_SERVICE.getName(), context.getVariable(IS_ROOT_SERVICE.getName()));
        model.addAttribute(IS_REST_SERVICE.getName(), context.getVariable(IS_REST_SERVICE.getName()));
        model.addAttribute(COMPONENT_INTEGRATION_TYPE.getName(), context.getVariable(COMPONENT_INTEGRATION_TYPE.getName()));
        model.addAttribute(COMPLETE_INTERFACE_NAME.getName(), context.getVariable(COMPLETE_INTERFACE_NAME.getName()));
        model.addAttribute(API_CREATED.getName(), context.getVariable(API_CREATED.getName()));
    }
}