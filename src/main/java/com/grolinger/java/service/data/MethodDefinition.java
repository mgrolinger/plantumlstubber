package com.grolinger.java.service.data;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Builder
public class MethodDefinition {
    @Builder.Default
    @Getter
    private List<String> methods = new LinkedList<>();
}
