package com.grolinger.java.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SimpleController {
    private Map<String, String> colorNameMapper = new HashMap<>();

    private Map<String, String> aliasMapper = new HashMap<>();

    private SimpleController() {
        colorNameMapper.put("integration", "INTEGRATION_COLOR_CONNECTION");
        colorNameMapper.put("resource", "RESOURCE_DOMAIN_COLOR_CONNECTION");
        colorNameMapper.put("financial", "FINANCIAL_DOMAIN_COLOR_COLOR");
        colorNameMapper.put("customer", "CUSTOMER_DOMAIN_COLOR_CONNECTION");
        colorNameMapper.put("external", "EXTERNAL_INTERFACE_COLOR_CONNECTION");

        aliasMapper.put("esb", "atlas");
        aliasMapper.put("atlas", "esb");
    }

    @GetMapping("/service/{serviceName}/interface/{interfaceName}/color/{colorName}")
    public String component(Model model, @PathVariable String serviceName, @PathVariable String interfaceName, @PathVariable String colorName) {
        model.addAttribute("serviceName", serviceName);
        model.addAttribute("interfaceName", interfaceName);
        model.addAttribute("connectionColor", colorNameMapper.get(colorName));
        model.addAttribute("colorName", colorName);
        return "component";
    }

    @GetMapping("/application/{applicationName}/service/{serviceName}/interface/{interfaceName}/color/{colorName}")
    public String application(Model model, @PathVariable String applicationName, @PathVariable String serviceName, @PathVariable String interfaceName, @PathVariable String colorName) {
        model.addAttribute("applicationName", applicationName);
        model.addAttribute("serviceName", serviceName);
        model.addAttribute("interfaceName", interfaceName);
        model.addAttribute("connectionColor", colorNameMapper.get(colorName));
        model.addAttribute("colorName", colorName);
        return "componentExport";
    }
}