package com.grolinger.java.controller;

import com.grolinger.java.config.Service;
import com.grolinger.java.service.NameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.grolinger.java.controller.ContextVariables.*;

@Controller
public class SingleExportController {
    private Map<String, String> aliasMapper = new HashMap<>();
    private final NameService nameService;

    @Autowired
    private SingleExportController(NameService nameService) {
        aliasMapper.put("esb", "atlas");
        aliasMapper.put("atlas", "esb");
        this.nameService = nameService;
    }

    @GetMapping("/component/{applicationName}/service/{serviceName}/interface/{interfaceName}/color/{colorName}/{integrationType}")
    public String singleComponent(Model model, @PathVariable final String applicationName, @PathVariable final String serviceName, @PathVariable final String interfaceName, @PathVariable final String colorName, @PathVariable final String integrationType) {
        prepareModel(model, applicationName, serviceName, interfaceName, colorName, integrationType, null);
        return "componentExport";
    }

    @PostMapping(value = "/component", produces = "text/html", consumes = "application/json")
    public String singleComponent(Model model, @org.jetbrains.annotations.NotNull @RequestBody Service service) {
        prepareModel(model, service.getApplication(), service.getServiceName(), service.getInterfaceName(), service.getColorName(), service.getColorName(), null);
        return "componentExport";
    }


    @GetMapping("/sequence/{applicationName}/service/{serviceName}/interface/{interfaceName}/color/{colorName}/{integrationType}/order/{orderPrio}")
    public String singleSequence(Model model, @PathVariable final String applicationName, @PathVariable final String serviceName, @PathVariable final String interfaceName, @PathVariable final String colorName, @PathVariable final String integrationType, @PathVariable final int orderPrio) {
        prepareModel(model, applicationName, serviceName, interfaceName, colorName, integrationType, orderPrio);
        return "sequenceExport";
    }

    @PostMapping(value = "/sequence", produces = "text/html", consumes = "application/json")
    public String singleSequence(Model model, @org.jetbrains.annotations.NotNull @RequestBody Service service) {
        prepareModel(model, service.getApplication(), service.getServiceName(), service.getInterfaceName(), service.getColorName(), service.getColorName(), null);
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
    private void prepareModel(Model model, @PathVariable String applicationName, @PathVariable String serviceName, @PathVariable String interfaceName, @PathVariable String colorName, @PathVariable String integrationType, @PathVariable Integer orderPrio) {
        model.addAttribute(DATE_CREATED.getName(), LocalDate.now());
        model.addAttribute(PATH_TO_COMMON_FILE.getName(), "../");
        String applicationNameNew = nameService.replaceUnwantedCharacters(applicationName, false);
        model.addAttribute(APPLICATION_NAME.getName(), StringUtils.capitalize(applicationNameNew));
        String applicationNameShort = aliasMapper.getOrDefault(applicationNameNew.toLowerCase(), applicationNameNew);
        model.addAttribute(APPLICATION_NAME_SHORT.getName(), applicationNameShort.toLowerCase());
        model.addAttribute(SERVICE_NAME.getName(), serviceName);
        model.addAttribute(INTERFACE_NAME.getName(), interfaceName);
        model.addAttribute(COLOR_TYPE.getName(), DomainColorMapper.getByType(colorName).getStereotype());
        model.addAttribute(CONNECTION_COLOR.getName(), ConnectionColorMapper.getByType(colorName));
        model.addAttribute(COLOR_NAME.getName(), DomainColorMapper.getByType(colorName));
        if (orderPrio != null) {
            model.addAttribute(SEQUENCE_PARTICIPANT_ORDER.getName(), orderPrio);
        }
        Boolean isRestService = integrationType.toUpperCase().startsWith(REST.getName());
        model.addAttribute(IS_REST_SERVICE.getName(), isRestService);
        model.addAttribute(COMPONENT_INTEGRATION_TYPE.getName(), "INTEGRATION_TYPE(" + integrationType + ")");
        model.addAttribute(COMPLETE_API_PATH.getName(), StringUtils.capitalize(applicationNameNew) + StringUtils.capitalize(serviceName) + StringUtils.capitalize(interfaceName) + "Int");
        model.addAttribute(API_CREATED.getName(), applicationName.toUpperCase() + "_API" + serviceName.toUpperCase() + Constants.NAME_SEPARATOR.getValue() + interfaceName.toUpperCase() + "_CREATED");
    }
}