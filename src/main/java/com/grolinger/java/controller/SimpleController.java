package com.grolinger.java.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class SimpleController {
    private TreeNode<String> components = new TreeNode<>("Service");
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
        return "component";
    }

    @GetMapping("/start")
    public String files(Model model) throws IOException {

        List<Path> paths = new LinkedList<>();
        Files.walk(Paths.get("M:\\Documents\\_Dokumente\\UMLServiceDefinitions\\Service"))
                .filter(Files::isRegularFile)
                .filter(it -> it.toString().endsWith(".iuml"))
                .forEach(p -> paths.add(p));

        for (Path path : paths) {
            String[] file = splitFilename(path);
            String previousNode = "";
            for (String currentNode: file) {
                if (currentNode.endsWith(".iuml")) {
                    //if(components.contains(currentNode))
                        break;
                    //components.add()
                }else{

                }

            }
            //getList(previousNode).add(file.toString());
        }


        // model.addAttribute("paths", paths.stream().toArray(String[]::new));
        model.addAttribute("files", components);

        return "start";
    }

    @PostMapping("/generate")
    public String generate(@ModelAttribute("command") FormCommand command, Model model) {
        System.out.println(">>>>>>>>>>>" + command.multiCheckboxSelectedValues);
        return "generate";

    }

    private String[] splitFilename(Path filename) {
        return filename.toString().replace("M:\\Documents\\_Dokumente\\UMLServiceDefinitions\\Service\\", "").split("\\\\");
    }

    private List<String> getList(String newComponent) {
        //if (components.containsKey(newComponent)) {
        //} else {
        //    components.put(newComponent, new LinkedList<>());
        //}
        return new LinkedList<>();//components.get(newComponent);
    }
}