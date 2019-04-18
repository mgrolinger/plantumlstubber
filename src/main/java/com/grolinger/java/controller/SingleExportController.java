package com.grolinger.java.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Controller
public class SingleExportController {
    private Map<String, String> aliasMapper = new HashMap<>();

    private SingleExportController() {
        aliasMapper.put("esb", "atlas");
        aliasMapper.put("atlas", "esb");
    }

    @GetMapping("/application/{applicationName}/service/{serviceName}/interface/{interfaceName}/color/{colorName}/{integrationType}")
    public String application(Model model, @PathVariable final String applicationName, @PathVariable final String serviceName, @PathVariable final String interfaceName, @PathVariable final String colorName,@PathVariable final String integrationType) {
        model.addAttribute("dateCreated", LocalDate.now());
        model.addAttribute("commonPath", "../");
        String applicationNameNew = getReplaceUnwantedCharacters(applicationName, false);
        model.addAttribute("applicationName", StringUtils.capitalize(applicationNameNew));
        String applicationNameShort = aliasMapper.getOrDefault(applicationNameNew.toLowerCase(), applicationNameNew);
        model.addAttribute("applicationNameShort", applicationNameShort.toLowerCase());
        model.addAttribute("serviceName", serviceName);
        model.addAttribute("interfaceName", interfaceName);
        model.addAttribute("colorType", DomainColorMapper.getByType(colorName).getStereotype());
        model.addAttribute("connectionColor", ConnectionColorMapper.getByType(colorName));
        model.addAttribute("colorName", DomainColorMapper.getByType(colorName));
        Boolean isRestService =  integrationType.toLowerCase().startsWith("rest");
        model.addAttribute("isRestService", isRestService);
        model.addAttribute("integrationType", "INTEGRATION_TYPE("+ integrationType+")");
        model.addAttribute("COMPLETE_INTERFACE_PATH", StringUtils.capitalize(applicationNameNew) +  StringUtils.capitalize(serviceName) + StringUtils.capitalize(interfaceName) + "Int");
        model.addAttribute("API_CREATED", applicationName.toUpperCase() + "_API" +serviceName.toUpperCase() + "_" + interfaceName.toUpperCase() + "_CREATED");
        return "componentExport";
    }


    private String getReplaceUnwantedCharacters(final String name, final boolean replaceDotsOnly) {
        String newName = name.replace('.', '_');
        if (!replaceDotsOnly)
            newName = newName.replace('/', '_');
        return newName;
    }
}