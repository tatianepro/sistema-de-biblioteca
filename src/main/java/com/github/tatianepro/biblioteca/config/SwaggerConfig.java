package com.github.tatianepro.biblioteca.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    private final String basePackage = "com.github.tatianepro.biblioteca.api.resource";

    @Value("${spring.config-swagger.name}")
    private String name;
    @Value("${spring.config-swagger.url}")
    private String url;
    @Value("${spring.config-swagger.email}")
    private String email;
    @Value("${spring.config-swagger.title}")
    private String title;
    @Value("${spring.config-swagger.description}")
    private String description;
    @Value("${spring.config-swagger.version}")
    private String version;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .contact(contact())
                .build();
    }

    private Contact contact() {
        return new Contact(name, url, email);
    }
}
