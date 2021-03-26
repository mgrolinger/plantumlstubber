package com.grolinger.java.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenAPIConfig {
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("")
                .pathsToMatch("/export/**")
                .build();
    }

    @Bean
    public OpenAPI apiInfo(@Value("${app.name}") String title, @Value("${app.version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(appVersion)
                        .description("Generator for PlantUML application/service stubs.")
                        .license(new License()
                                .name("Apache V2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0"))
                        .contact(new Contact()
                                .name("Michael Grolinger")
                                .url("https://de.linkedin.com/in/mgrolinger")
                                .email("m.grolinger+plantuml@gmail.com"))
                );
    }
}