package com.grolinger.java.service.adapter.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
public class StaticFileService {
    private StaticFileService() {
        //hide
    }

    public static void readFile(final String inputFileName, final Path outputFile) {
        try (Stream<String> stream = Files.lines(Paths.get(inputFileName))) {
            log.info("Read file:{} and output it to: {}", inputFileName, outputFile);
            stream.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
