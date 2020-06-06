package com.grolinger.java.service.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates a color from the domainColor name. Static colors are allowed and need to be configured here.
 * The colors are used for default.skin
 */
@Slf4j
@NoArgsConstructor
public class ColorGenerator {
    private static final Map<String, String> colorMap = new HashMap<String, String>() {{
        // SID
        put("customer", "Orange");
        put("external", "D4D4CD");
        put("financial", "Gold");
        put("integration", "DeepSkyBlue");
        put("resource", "LightGreen");
        // BiPRO
        put("authentifizierung", "537499");
        put("bestand", "5C98E1");
        put("risikodaten", "ffc042");
        put("spezifisches", "LightGrey");
        put("suche", "4CA8FF");
        put("taa", "15c6f2");
        put("uebermittlung", "96e8c3");
    }};

    private static final Map<String, String> connectionColorMap = new HashMap<String, String>() {{
        // SID
        put("customer", "Orange");
        put("external", "DimGrey");
        put("financial", "GoldenRod");
        put("integration", "DeepSkyBlue");
        put("resource", "SeaGreen");
        // BiPRO
        put("authentifizierung", "537499");
        put("bestand", "5C98E1");
        put("risikodaten", "ffc042");
        put("spezifisches", "LightGrey");
        put("suche", "4CA8FF");
        put("taa", "15c6f2");
        put("uebermittlung", "96e8c3");
    }};

    public static String getColorCode(final String domainColor) {
        String dC = domainColor.toLowerCase();
        if (colorMap.containsKey(dC)) {
            return colorMap.get(dC);
        }

        return generateColorFromHash(dC);
    }

    public static String getConnectionColorCode(final String domainColor) {
        String dC = domainColor.toLowerCase();
        if (connectionColorMap.containsKey(dC)) {
            return connectionColorMap.get(dC);
        }

        return generateColorFromHash(dC);
    }

    private static String generateColorFromHash(String domainColor) {
        byte[] hash = new byte[0];
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(domainColor.getBytes(StandardCharsets.US_ASCII));
            hash = digest.digest();
        } catch (NoSuchAlgorithmException exp) {
            log.error("An error occured: {}", exp.getMessage());
        }
        StringBuilder color = new StringBuilder();
        if (hash.length >= 6) {
            for (byte b : hash) {
                color.append(b);
            }
            System.out.println(color + " - " + hash.length);
            color = new StringBuilder(color.toString().replaceAll("-", "").substring(0, 6));
        } else {
            color = new StringBuilder("FF0000");
        }
        return color.toString();
    }
}
