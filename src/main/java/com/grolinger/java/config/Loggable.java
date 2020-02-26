package com.grolinger.java.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common Logger interface that provides a logger for the implementing class.
 */
public interface Loggable {

    /**
     * provides a logger for the current class
     *
     * @return logger for current class
     */
    default Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }
}