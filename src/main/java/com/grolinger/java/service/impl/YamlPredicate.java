package com.grolinger.java.service.impl;

import java.nio.file.Path;

class YamlPredicate {
    private YamlPredicate() {
    }

    static boolean isYamlFile(Path path) {
        return path.toAbsolutePath().toString().toLowerCase().contains(".yaml");
    }
}