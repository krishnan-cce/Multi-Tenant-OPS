package com.kris.orderservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Multi-Tenant Order Processing Service API",
                description = "API documentation for Multi-Tenant Order Processing Service",
                version = "1.0.0",
                contact = @Contact(name = "Krishnan KV", email = "krishnanvenugopal707@gmail.com")
        ),
        servers = {
                @Server(url = "/", description = "Default Server URL")
        }
)

public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Multi-Tenant Order Processing Service API")
                        .description("Comprehensive API documentation for Multi-Tenant Order Processing Service")
                        .version("1.0.0")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Krishnan KV")
                                .email("krishnanvenugopal707@gmail.com")));
    }

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfigProperties() {
        SwaggerUiConfigProperties props = new SwaggerUiConfigProperties();
        props.setPath("/swagger-ui.html");
        props.setOperationsSorter("method");
        props.setTryItOutEnabled(true);
        props.setFilter("true");
        return props;
    }

}