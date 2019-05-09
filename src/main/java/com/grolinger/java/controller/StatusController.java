package com.grolinger.java.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    @GetMapping(value = "/status", produces = "text/plain")
    public String status() {
        return "OK";
    }
}
