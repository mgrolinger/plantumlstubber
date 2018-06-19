package com.grolinger.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.grolinger.java.ServiceGenerator;
import com.grolinger.java.config.Services;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class ExportController {

    private List<Services> servicesList = new LinkedList<>();

    @Autowired
    private SpringTemplateEngine templateEngine;

    private Map<String, String> colorNameMapper = new HashMap<>();

    private Map<String, String> aliasMapper = new HashMap<>();

    private ExportController() {
        colorNameMapper.put("integration", "INTEGRATION_COLOR_CONNECTION");
        colorNameMapper.put("resource", "RESOURCE_DOMAIN_COLOR_CONNECTION");
        colorNameMapper.put("financial", "FINANCIAL_DOMAIN_COLOR_COLOR");
        colorNameMapper.put("customer", "CUSTOMER_DOMAIN_COLOR_CONNECTION");
        colorNameMapper.put("external", "EXTERNAL_INTERFACE_COLOR_CONNECTION");

        aliasMapper.put("esb", "atlas");
    }

    @GetMapping("/export")
    public void component(Model model) throws IOException {

        // Find all yaml files
        Files.list(Paths.get("./target/classes/"))
                .filter(Files::isRegularFile)
                .filter(YamlPredicate::isYamlFile)
                .forEach(this::mapYamls);

        // Iterate over yaml files
        String basepath = "./target/";
        String path = "";
        Set<String> dirsCreate = new HashSet<>();
        for (Services services : servicesList) {
            Context context = new Context();
            context.setVariable("dateCreated", LocalDateTime.now());
            String applicationName = services.getApplication();
            if (!dirsCreate.contains(applicationName)) {
                context.setVariable("commonPath", "../");
                path = basepath + applicationName + "/";
                Files.createDirectories(Paths.get(path));
                dirsCreate.add(applicationName);
            }
            String colorName = services.getColor();
            context.setVariable("applicationName", applicationName);
            String applicationNameShort = aliasMapper.containsKey(applicationName.toLowerCase()) ? aliasMapper.get(applicationName.toLowerCase()) : applicationName;
            context.setVariable("applicationNameShort", applicationNameShort);
            context.setVariable("colorType", "<<" + colorName.toLowerCase() + ">>");
            context.setVariable("connectionColor", colorNameMapper.get(colorName.toLowerCase()));

            for (Map.Entry entry : services.getServices().entrySet()) {
                String serviceName = (String) entry.getKey();
                if (serviceName.equalsIgnoreCase("EMPTY")) {
                    //serviceName = applicationName;
                    //context.setVariable("serviceName", serviceName);
                    context.setVariable("commonPath", "../");
                    context.setVariable("isRootService", true);
                } else {
                    if (applicationName.equalsIgnoreCase("esb")) {
                        context.setVariable("isRootService", false);
                    } else {
                        context.setVariable("isRootService", true);
                    }
                    context.setVariable("commonPath", "../../");
                    if (!dirsCreate.contains(applicationName + serviceName)) {
                        path = basepath + applicationName + "/" + serviceName + "/";
                        Files.createDirectories(Paths.get(path));
                        dirsCreate.add(applicationName + serviceName);
                        context.setVariable("serviceName", serviceName);
                    }
                }

                String[] interfaces = (String[]) entry.getValue();
                for (String interfaceName : interfaces) {
                    //interface
                    context.setVariable("interfaceName", interfaceName);

                    try (Writer writer = new FileWriter(path + interfaceName + ".iuml")) {
                        writer.write(templateEngine.process("componentExport.html", context));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private static class YamlPredicate {
        static boolean isYamlFile(Path path) {
            return path.toAbsolutePath().toString().toLowerCase().contains(".yaml");
        }
    }


    private void mapYamls(Path path) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Services services = mapper.readValue(path.toFile(), Services.class);
            System.out.println(ReflectionToStringBuilder.toString(services, ToStringStyle.MULTI_LINE_STYLE));
            servicesList.add(services);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}