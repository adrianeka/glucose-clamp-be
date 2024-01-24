package com.tujuhsembilan.bookrecipe.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    private final String localUrl;
    private final String devUrl;
    private final String moduleName;
    private final String moduleDesc;
    private final String apiVersion;

    public OpenAPIConfig(
            @Value("${tujuhsembilan.openapi.local-url}") String localUrl,
            @Value("${tujuhsembilan.openapi.dev-url}") String devUrl,
            @Value("${tujuhsembilan.openapi.module-name}") String moduleName,
            @Value("${tujuhsembilan.openapi.module-desc}") String moduleDesc,
            @Value("${tujuhsembilan.openapi.api-version}") String apiVersion) {
        this.localUrl = localUrl;
        this.devUrl = devUrl;
        this.moduleName = moduleName;
        this.moduleDesc = moduleDesc;
        this.apiVersion = apiVersion;
    }

    @Bean
    public OpenAPI myOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl(localUrl);
        localServer.setDescription("Server URL Local");

        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL Development");

        final String securitySchemeName = "bearerAuth";

        Components components = new Components()
                .addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
        );

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title(moduleName)
                .version(apiVersion)
                .description(moduleDesc)
                .license(mitLicense);

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(components)
                .info(info)
                .servers(List.of(localServer, devServer));
    }
}
