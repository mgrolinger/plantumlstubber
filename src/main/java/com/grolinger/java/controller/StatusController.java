package com.grolinger.java.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This service returns OK to be able to get a status for a jenkins job.
 * When OK the service is started and we can call the rest api automatically.
 */
@RestController
public class StatusController {
    @GetMapping(value = "/status", produces = "text/plain")
    public String status() {
        return "OK";
    }
}
