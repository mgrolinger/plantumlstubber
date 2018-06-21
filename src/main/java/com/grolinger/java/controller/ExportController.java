package com.grolinger.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.grolinger.java.config.Services;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    private static final String EMPTY = "EMPTY";
    private static final String PATH_SEPARATOR = "/";
    private static final String DIR_UP = "../";
    private List<Services> applicationList = new LinkedList<>();

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

    @SuppressWarnings("squid:S2095")
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

        for (Services services : applicationList) {
            // Prepare examplefile for every Application
            StringBuilder exampleFile = new StringBuilder();
            exampleFile.append("@startuml\n");
            exampleFile.append("' generated on " + LocalDateTime.now() + "\n");
            exampleFile.append("'!define DETAILED\n!define UML_STRICT\n\n\n");

            Context context = new Context();
            context.setVariable("dateCreated", LocalDateTime.now());
            String applicationName = services.getApplication();
            applicationName = getReplaceUnwantedCharacters(applicationName);
            if (!dirsCreate.contains(applicationName)) {
                context.setVariable("commonPath", DIR_UP);
                path = basepath + applicationName + PATH_SEPARATOR;
                Files.createDirectories(Paths.get(path));
                dirsCreate.add(applicationName);
            }
            String colorName = services.getColor();
            context.setVariable("applicationName", applicationName);
            String applicationNameShort = aliasMapper.getOrDefault(applicationName.toLowerCase(), applicationName);
            context.setVariable("applicationNameShort", applicationNameShort);
            context.setVariable("colorType", "<<" + colorName.toLowerCase() + ">>");
            context.setVariable("connectionColor", colorNameMapper.get(colorName.toLowerCase()));

            if(services.getServices() == null){
                break;
            }
            for (Map.Entry entry : services.getServices().entrySet()) {

                String serviceName = (String) entry.getKey();
                serviceName = getReplaceUnwantedCharacters(serviceName);
                if (serviceName.equalsIgnoreCase(EMPTY)) {
                    context.setVariable("commonPath", DIR_UP);
                    context.setVariable("isRootService", true);
                } else {
                    context.setVariable("isRootService", false);
                    context.setVariable("commonPath", DIR_UP+DIR_UP);
                    if (!dirsCreate.contains(applicationName + serviceName)) {
                        path = basepath + applicationName + PATH_SEPARATOR + serviceName + PATH_SEPARATOR;
                        Files.createDirectories(Paths.get(path));
                        dirsCreate.add(applicationName + serviceName);
                        context.setVariable("serviceName", serviceName);
                    }
                }

                String[] interfaces = (String[]) entry.getValue();
                if (!processInterface(path, exampleFile, context, applicationName, serviceName, interfaces)) break;

            }
            exampleFile.append("@enduml");
            try (Writer writer = new FileWriter(basepath + applicationName + PATH_SEPARATOR + applicationName + "_example.puml")) {
                writer.write(exampleFile.toString());
            } catch (IOException ignore) {
                // do nothing
            }
        }
    }

    private boolean processInterface(String path, StringBuilder exampleFile, Context context, String applicationName, String serviceName, String[] interfaces) {
        if(interfaces == null){
            return false;
        }
        for (String interfaceName : interfaces) {
            interfaceName = getReplaceUnwantedCharacters(interfaceName);
            //interface
            context.setVariable("interfaceName", interfaceName);

            try (Writer writer = new FileWriter(path + interfaceName + ".iuml")) {
                String pEx = (serviceName.equalsIgnoreCase(EMPTY)) ? "" : serviceName + PATH_SEPARATOR;
                exampleFile.append("!include " + pEx + interfaceName + ".iuml \n");
                String serviceCall = (serviceName.equalsIgnoreCase(EMPTY)) ? applicationName : applicationName+"_"+serviceName;
                System.out.println("Export call: "+serviceCall+ "_" + interfaceName + "()");
                exampleFile.append(StringUtils.capitalize(serviceCall) + "_" + interfaceName + "()\n\n");
                writer.write(templateEngine.process("componentExport.html", context));
            } catch (IOException ignore) {
                // do nothing
            }
        }
        return true;
    }

    private String getReplaceUnwantedCharacters(String name) {
        return name.replace('.', '_');
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
            applicationList.add(services);
        } catch (Exception ignore) {
            //Do nothing
            System.err.println(ignore);
        }
    }
}