package com.grolinger.java.service.adapter.importdata.impl;

import java.nio.file.Path;

/**
 * Predicate to check if this is a yaml file or not.
 */
class YamlPredicate {
    private YamlPredicate() {
    }

    static boolean isYamlFile(Path path) {
        return path.toAbsolutePath().toString().toLowerCase().contains(".yaml");
    }
}