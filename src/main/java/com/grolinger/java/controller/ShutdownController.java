package com.grolinger.java.controller;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownController implements ApplicationContextAware {

    private ApplicationContext context;

    /**
     * This route is used to shutdown the container, while running inside Intellij Community
     */
    //FIXME profile
    @PostMapping("/shutdown")
    public void shutdownContext() {
        ((ConfigurableApplicationContext) context).close();
        SpringApplication.exit(context, () -> 0);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }
}